package com.publicolor.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class ReporteClienteItem {
    private Long clientId;
    private String clientName;
    private BigDecimal totalSold;
    private BigDecimal totalPending;
}
