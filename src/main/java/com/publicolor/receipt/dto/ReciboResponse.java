package com.publicolor.receipt.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class ReciboResponse {
    private Long consecutiveNumber;
    private LocalDateTime generatedAt;
    private String businessName;
    private String clientName;
    private String jobCode;
    private List<ReciboItemResponse> items;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal pendingAmount;
    /** Cuánto de totalPaid vino de saldo a favor de un trabajo anterior (no de plata recibida ahora). */
    private BigDecimal creditApplied;
    /** Saldo a favor que el cliente todavía tiene libre después de este trabajo. */
    private BigDecimal remainingCredit;
    private String notes;
    private String disclaimer;
}
