package com.publicolor.dashboard.service.impl;

import com.publicolor.dashboard.dto.DashboardResponse;
import com.publicolor.dashboard.dto.IngresoChartPoint;
import com.publicolor.dashboard.service.DashboardService;
import com.publicolor.finance.expense.repository.EgresoRepository;
import com.publicolor.finance.income.model.IngresoManual;
import com.publicolor.finance.income.repository.IngresoManualRepository;
import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.repository.TrabajoRepository;
import com.publicolor.job.service.TrabajoService;
import com.publicolor.payment.model.OrigenPago;
import com.publicolor.payment.model.Pago;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final TrabajoRepository trabajoRepo;
    private final PagoRepository pagoRepo;
    private final IngresoManualRepository ingresoRepo;
    private final EgresoRepository egresoRepo;
    private final TrabajoService trabajoService;

    @Override
    public DashboardResponse obtener() {
        LocalDate today = TimeUtil.today();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        BigDecimal soldToday = trabajoRepo.sumVendidoEnFecha(today);
        BigDecimal soldMonth = trabajoRepo.sumVendidoEntreFechas(firstDayOfMonth, today);
        BigDecimal soldHistoric = trabajoRepo.sumVendidoHistorico();
        BigDecimal paidHistoric = pagoRepo.sumCashAmountHistorico();
        BigDecimal pendingTotal = soldHistoric.subtract(paidHistoric);

        BigDecimal receivedToday = pagoRepo.sumCashAmountEnFecha(today).add(ingresoRepo.sumAmountEnFecha(today));
        BigDecimal receivedMonth = pagoRepo.sumCashAmountEntreFechas(firstDayOfMonth, today)
                .add(ingresoRepo.sumAmountEntreFechas(firstDayOfMonth, today));

        BigDecimal expensesToday = egresoRepo.sumAmountEnFecha(today);
        BigDecimal expensesMonth = egresoRepo.sumAmountEntreFechas(firstDayOfMonth, today);

        long openAccounts = trabajoRepo.countByStatus(EstadoCuenta.ABIERTA);

        return DashboardResponse.builder()
                .soldToday(soldToday)
                .soldMonth(soldMonth)
                .soldHistoric(soldHistoric)
                .pendingTotal(pendingTotal)
                .receivedToday(receivedToday)
                .receivedMonth(receivedMonth)
                .expensesToday(expensesToday)
                .expensesMonth(expensesMonth)
                .balanceToday(receivedToday.subtract(expensesToday))
                .balanceMonth(receivedMonth.subtract(expensesMonth))
                .openAccounts(openAccounts)
                .recentJobs(trabajoService.listarRecientes())
                .build();
    }

    @Override
    public List<IngresoChartPoint> obtenerGraficoIngresos(String granularity) {
        boolean porMes = "month".equalsIgnoreCase(granularity);
        LocalDate today = TimeUtil.today();
        LocalDate from = porMes ? today.withDayOfYear(1) : today.withDayOfMonth(1);

        // Suma diaria real (pagos en efectivo + ingresos manuales), sin días futuros.
        Map<LocalDate, BigDecimal> porDia = new TreeMap<>();
        for (Pago p : pagoRepo.findByPaymentDateBetweenAndOriginAndAnnulledFalseOrderByPaymentDateAsc(from, today, OrigenPago.CASH)) {
            porDia.merge(p.getPaymentDate(), p.getAmount(), BigDecimal::add);
        }
        for (IngresoManual i : ingresoRepo.findByIncomeDateBetweenAndAnnulledFalseOrderByIncomeDateAsc(from, today)) {
            porDia.merge(i.getIncomeDate(), i.getAmount(), BigDecimal::add);
        }

        List<IngresoChartPoint> puntos = new ArrayList<>();
        if (porMes) {
            DateTimeFormatter etiquetaMes = DateTimeFormatter.ofPattern("yyyy-MM");
            Map<String, BigDecimal> porMesAcumulado = new TreeMap<>();
            porDia.forEach((fecha, monto) ->
                    porMesAcumulado.merge(fecha.format(etiquetaMes), monto, BigDecimal::add));
            LocalDate cursor = from.withDayOfMonth(1);
            LocalDate limite = today.withDayOfMonth(1);
            while (!cursor.isAfter(limite)) {
                String etiqueta = cursor.format(etiquetaMes);
                puntos.add(IngresoChartPoint.builder()
                        .period(etiqueta)
                        .amount(porMesAcumulado.getOrDefault(etiqueta, BigDecimal.ZERO))
                        .build());
                cursor = cursor.plusMonths(1);
            }
        } else {
            long dias = ChronoUnit.DAYS.between(from, today);
            for (long i = 0; i <= dias; i++) {
                LocalDate fecha = from.plusDays(i);
                puntos.add(IngresoChartPoint.builder()
                        .period(fecha.toString())
                        .amount(porDia.getOrDefault(fecha, BigDecimal.ZERO))
                        .build());
            }
        }
        return puntos;
    }
}
