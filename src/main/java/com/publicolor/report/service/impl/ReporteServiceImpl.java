package com.publicolor.report.service.impl;

import com.publicolor.finance.expense.repository.EgresoRepository;
import com.publicolor.finance.income.repository.IngresoManualRepository;
import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.model.Trabajo;
import com.publicolor.job.repository.TrabajoRepository;
import com.publicolor.job.repository.TrabajoSpecifications;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.report.dto.ReporteClienteItem;
import com.publicolor.report.dto.ReporteConceptoItem;
import com.publicolor.report.dto.ReporteResponse;
import com.publicolor.report.dto.ReporteTrabajoItem;
import com.publicolor.report.service.ReporteService;
import com.publicolor.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private static final LocalDate DESDE_SIEMPRE = LocalDate.of(2000, 1, 1);

    private final TrabajoRepository trabajoRepo;
    private final PagoRepository pagoRepo;
    private final IngresoManualRepository ingresoRepo;
    private final EgresoRepository egresoRepo;

    @Override
    public ReporteResponse generar(Long clientId, EstadoCuenta status, LocalDate from, LocalDate to) {
        LocalDate desde = from != null ? from : DESDE_SIEMPRE;
        LocalDate hasta = to != null ? to : TimeUtil.today();

        List<Trabajo> trabajos = trabajoRepo.findAll(TrabajoSpecifications.conFiltros(clientId, status, from, to));

        BigDecimal totalVendido = trabajos.stream()
                .map(Trabajo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Dos mapas separados a propósito: "pagadoPorTrabajo" (efectivo + crédito aplicado) sirve
        // para saber si CADA trabajo puntual está saldado. "pagadoCashPorTrabajo" (solo efectivo real)
        // sirve para los TOTALES agregados — si se usara el primero ahí, el crédito reasignado entre
        // dos trabajos del mismo cliente se contaría dos veces (una al "sobrar" en el trabajo de
        // origen, otra al "pagar" el trabajo de destino) y el pendiente saldría de menos.
        Map<Long, BigDecimal> pagadoPorTrabajo = new LinkedHashMap<>();
        Map<Long, BigDecimal> pagadoCashPorTrabajo = new LinkedHashMap<>();
        for (Trabajo t : trabajos) {
            pagadoPorTrabajo.put(t.getId(), pagoRepo.sumAmountByTrabajoId(t.getId()));
            pagadoCashPorTrabajo.put(t.getId(), pagoRepo.sumCashAmountByTrabajoId(t.getId()));
        }
        BigDecimal totalPagadoCashDeEstosTrabajos = pagadoCashPorTrabajo.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPendiente = totalVendido.subtract(totalPagadoCashDeEstosTrabajos);

        BigDecimal totalIngresosManuales = ingresoRepo.sumAmountEntreFechas(desde, hasta);
        BigDecimal totalEgresos = egresoRepo.sumAmountEntreFechas(desde, hasta);
        BigDecimal totalRecibido = pagoRepo.sumCashAmountEntreFechas(desde, hasta).add(totalIngresosManuales);
        BigDecimal balance = totalRecibido.subtract(totalEgresos);

        Map<Long, ReporteClienteItem> porCliente = new LinkedHashMap<>();
        for (Trabajo t : trabajos) {
            Long cid = t.getCliente().getId();
            BigDecimal pagadoCash = pagadoCashPorTrabajo.get(t.getId());
            ReporteClienteItem actual = porCliente.get(cid);
            BigDecimal vendidoAcumulado = (actual == null ? BigDecimal.ZERO : actual.getTotalSold()).add(t.getTotalAmount());
            BigDecimal pendienteAcumulado = (actual == null ? BigDecimal.ZERO : actual.getTotalPending())
                    .add(t.getTotalAmount().subtract(pagadoCash));
            porCliente.put(cid, ReporteClienteItem.builder()
                    .clientId(cid)
                    .clientName(t.getCliente().getName())
                    .totalSold(vendidoAcumulado)
                    .totalPending(pendienteAcumulado)
                    .build());
        }

        List<ReporteTrabajoItem> jobs = trabajos.stream()
                .map(t -> {
                    BigDecimal pagado = pagadoPorTrabajo.get(t.getId());
                    return ReporteTrabajoItem.builder()
                            .jobId(t.getId())
                            .code(t.getCode())
                            .clientName(t.getCliente().getName())
                            .jobDate(t.getJobDate())
                            .totalAmount(t.getTotalAmount())
                            .totalPending(t.getTotalAmount().subtract(pagado))
                            .status(t.getStatus().name())
                            .build();
                })
                .sorted((a, b) -> b.getJobDate().compareTo(a.getJobDate()))
                .toList();

        // El reporte principal: cada concepto (producto) vendido, uno por uno, sin agrupar —
        // aunque muestre el cliente al lado, no es un resumen por cliente.
        List<ReporteConceptoItem> concepts = trabajos.stream()
                .flatMap(t -> t.getConceptos().stream().map(c -> ReporteConceptoItem.builder()
                        .jobId(t.getId())
                        .jobCode(t.getCode())
                        .clientName(t.getCliente().getName())
                        .jobDate(t.getJobDate())
                        .productType(c.getTipoProducto().getName())
                        .description(c.getDescription())
                        .amount(c.getTotalAmount())
                        .build()))
                .sorted((a, b) -> b.getJobDate().compareTo(a.getJobDate()))
                .toList();

        return ReporteResponse.builder()
                .from(from)
                .to(to)
                .totalSold(totalVendido)
                .totalReceived(totalRecibido)
                .totalPending(totalPendiente)
                .totalManualIncomes(totalIngresosManuales)
                .totalExpenses(totalEgresos)
                .balance(balance)
                .byClient(List.copyOf(porCliente.values()))
                .jobs(jobs)
                .concepts(concepts)
                .build();
    }
}
