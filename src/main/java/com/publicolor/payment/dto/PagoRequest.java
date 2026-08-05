package com.publicolor.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class PagoRequest {

    @NotNull(message = "El trabajo es obligatorio.")
    private Long jobId;

    @NotNull(message = "El valor del pago es obligatorio.")
    @DecimalMin(value = "0.01", message = "El valor del pago debe ser mayor a cero.")
    private BigDecimal amount;

    @NotNull(message = "La fecha del pago es obligatoria.")
    private LocalDate paymentDate;

    private Long paymentMethodId;

    private String notes;

    /** Debe venir en true si el abono supera el saldo pendiente (confirmación explícita). */
    private boolean forceOverpay;
}
