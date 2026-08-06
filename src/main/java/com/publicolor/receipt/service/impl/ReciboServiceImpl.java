package com.publicolor.receipt.service.impl;

import com.publicolor.job.model.ConceptoTrabajo;
import com.publicolor.job.model.Trabajo;
import com.publicolor.job.repository.TrabajoRepository;
import com.publicolor.job.service.TrabajoService;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.receipt.dto.ReciboItemResponse;
import com.publicolor.receipt.dto.ReciboResponse;
import com.publicolor.receipt.model.ReciboCobro;
import com.publicolor.receipt.repository.ReciboCobroRepository;
import com.publicolor.receipt.service.ReciboService;
import com.publicolor.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ReciboServiceImpl implements ReciboService {

    private static final String DISCLAIMER =
            "Este documento corresponde a un cobro y no certifica la realización del pago.";

    private final TrabajoService trabajoService;
    private final TrabajoRepository trabajoRepo;
    private final PagoRepository pagoRepo;
    private final ReciboCobroRepository reciboRepo;

    @Override
    public ReciboResponse generar(Long jobId) {
        Trabajo trabajo = trabajoService.obtenerEntidad(jobId);

        ReciboCobro recibo = ReciboCobro.builder()
                .trabajo(trabajo)
                .consecutiveNumber(reciboRepo.siguienteConsecutivo())
                .build();
        ReciboCobro guardado = reciboRepo.save(recibo);

        BigDecimal totalPagado = pagoRepo.sumAmountByTrabajoId(jobId);
        BigDecimal pendiente = trabajo.getTotalAmount().subtract(totalPagado);
        BigDecimal creditoAplicado = pagoRepo.sumCreditAppliedByTrabajoId(jobId);

        Long clienteId = trabajo.getCliente().getId();
        BigDecimal compradoCliente = trabajoRepo.sumVendidoPorCliente(clienteId);
        BigDecimal pagadoEfectivoCliente = pagoRepo.sumCashAmountByClientId(clienteId);
        BigDecimal saldoRestante = pagadoEfectivoCliente.subtract(compradoCliente);
        if (saldoRestante.compareTo(BigDecimal.ZERO) < 0) {
            saldoRestante = BigDecimal.ZERO;
        }

        var items = trabajo.getConceptos().stream()
                .map(this::toItemResponse)
                .toList();

        return ReciboResponse.builder()
                .consecutiveNumber(guardado.getConsecutiveNumber())
                .generatedAt(TimeUtil.now())
                .businessName("Publicolor")
                .clientName(trabajo.getCliente().getName())
                .jobCode(trabajo.getCode())
                .items(items)
                .totalAmount(trabajo.getTotalAmount())
                .totalPaid(totalPagado)
                .pendingAmount(pendiente)
                .creditApplied(creditoAplicado)
                .remainingCredit(saldoRestante)
                .notes(trabajo.getNotes())
                .disclaimer(DISCLAIMER)
                .build();
    }

    private ReciboItemResponse toItemResponse(ConceptoTrabajo c) {
        return ReciboItemResponse.builder()
                .productType(c.getTipoProducto().getName())
                .description(c.getDescription())
                .finishes(c.getAcabados().stream().map(a -> a.getName()).toList())
                .laminations(c.getLaminados().stream().map(l -> l.getName()).toList())
                .notes(c.getNotes())
                .totalAmount(c.getTotalAmount())
                .build();
    }
}
