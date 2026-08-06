package com.publicolor.receipt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Exporta a PDF el recibo que el frontend ya generó y tiene en pantalla (mismo número
 * consecutivo, mismos totales) — no crea un recibo nuevo, solo lo renderiza en otro formato.
 */
@Getter @Setter
public class ReciboPdfRequest {

    @NotNull
    private Long consecutiveNumber;

    @NotNull
    private String clientName;

    @NotNull
    private String jobCode;

    @NotNull
    private List<ReciboPdfItemRequest> items;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private BigDecimal totalPaid;

    @NotNull
    private BigDecimal pendingAmount;

    private BigDecimal creditApplied;

    private BigDecimal remainingCredit;

    private String notes;
}
