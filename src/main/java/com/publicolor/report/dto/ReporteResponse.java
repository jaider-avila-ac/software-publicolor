package com.publicolor.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Builder
public class ReporteResponse {
    private LocalDate from;
    private LocalDate to;
    private BigDecimal totalSold;
    private BigDecimal totalReceived;
    private BigDecimal totalPending;
    private BigDecimal totalManualIncomes;
    private BigDecimal totalExpenses;
    private BigDecimal balance;
    private List<ReporteClienteItem> byClient;
}
