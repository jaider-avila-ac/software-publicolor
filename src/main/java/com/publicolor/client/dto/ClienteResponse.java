package com.publicolor.client.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
public class ClienteResponse {
    private Long id;
    private String name;
    private BigDecimal totalPurchased;
    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private LocalDateTime createdAt;
}
