package com.publicolor.payment.service.impl;

import com.publicolor.catalog.dto.LookupItem;
import com.publicolor.catalog.model.MetodoPago;
import com.publicolor.catalog.repository.MetodoPagoRepository;
import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.model.Trabajo;
import com.publicolor.job.service.TrabajoService;
import com.publicolor.payment.dto.PagoRequest;
import com.publicolor.payment.dto.PagoResponse;
import com.publicolor.payment.model.Pago;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.payment.service.PagoService;
import com.publicolor.shared.exception.ConfirmacionRequeridaException;
import com.publicolor.shared.exception.CuentaCanceladaException;
import com.publicolor.shared.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepo;
    private final MetodoPagoRepository metodoPagoRepo;
    private final TrabajoService trabajoService;

    @Override
    public PagoResponse registrar(PagoRequest req) {
        Trabajo trabajo = trabajoService.obtenerEntidad(req.getJobId());

        if (trabajo.getStatus() == EstadoCuenta.CANCELADA) {
            throw new CuentaCanceladaException("No se pueden registrar pagos sobre una cuenta cancelada.");
        }

        BigDecimal totalPagado = pagoRepo.sumAmountByTrabajoId(trabajo.getId());
        BigDecimal pendiente = trabajo.getTotalAmount().subtract(totalPagado);

        if (req.getAmount().compareTo(pendiente) > 0 && !req.isForceOverpay()) {
            throw new ConfirmacionRequeridaException(
                    "El pago supera el saldo pendiente ($" + pendiente + "). Confirmá para registrarlo de todas formas.");
        }

        MetodoPago metodoPago = null;
        if (req.getPaymentMethodId() != null) {
            metodoPago = metodoPagoRepo.findById(req.getPaymentMethodId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado."));
        }

        Pago pago = Pago.builder()
                .code(generarCodigoUnico())
                .trabajo(trabajo)
                .metodoPago(metodoPago)
                .amount(req.getAmount())
                .paymentDate(req.getPaymentDate())
                .notes(req.getNotes())
                .build();

        Pago guardado = pagoRepo.save(pago);
        trabajoService.recalcularEstado(trabajo);

        return toResponse(guardado);
    }

    /** Código único (ej. "PG-0001"); igual que en trabajos/egresos/ingresos, se verifica explícitamente antes de usarlo. */
    private String generarCodigoUnico() {
        String codigo = "PG-" + String.format("%04d", pagoRepo.siguienteConsecutivo());
        if (pagoRepo.existsByCode(codigo)) {
            throw new com.publicolor.shared.exception.NegocioException("No se pudo generar un código único para el pago. Intentá de nuevo.");
        }
        return codigo;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<PagoResponse> listarPorTrabajo(Long jobId) {
        return pagoRepo.findByTrabajo_IdOrderByPaymentDateDesc(jobId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<PagoResponse> listarPorCliente(Long clientId) {
        return pagoRepo.findByTrabajo_Cliente_IdOrderByPaymentDateDesc(clientId).stream().map(this::toResponse).toList();
    }

    @Override
    public PagoResponse anular(Long id, String reason) {
        Pago pago = pagoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado."));

        if (pago.isAnnulled()) {
            throw new com.publicolor.shared.exception.NegocioException("Este pago ya está anulado.");
        }

        pago.setAnnulled(true);
        pago.setAnnulledAt(com.publicolor.shared.util.TimeUtil.now());
        pago.setAnnulledReason(reason);
        Pago guardado = pagoRepo.save(pago);

        // El trabajo pierde ese pago de sus totales: puede volver a ABIERTA o PARCIALMENTE_PAGADA.
        trabajoService.recalcularEstado(pago.getTrabajo());

        return toResponse(guardado);
    }

    private PagoResponse toResponse(Pago p) {
        return PagoResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .jobId(p.getTrabajo().getId())
                .amount(p.getAmount())
                .paymentDate(p.getPaymentDate())
                .paymentMethod(p.getMetodoPago() == null ? null :
                        LookupItem.builder().id(p.getMetodoPago().getId()).name(p.getMetodoPago().getName()).build())
                .notes(p.getNotes())
                .origin(p.getOrigin().name())
                .createdAt(p.getCreatedAt())
                .annulled(p.isAnnulled())
                .annulledAt(p.getAnnulledAt())
                .annulledReason(p.getAnnulledReason())
                .build();
    }
}
