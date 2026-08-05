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
    private String jobTitle;
    private List<ReciboItemResponse> items;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal pendingAmount;
    private String notes;
    private String disclaimer;
}
