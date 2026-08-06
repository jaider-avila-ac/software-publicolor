package com.publicolor.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Builder
public class ReporteTrabajoItem {
    private Long jobId;
    private String code;
    private String clientName;
    private LocalDate jobDate;
    private BigDecimal totalAmount;
    private BigDecimal totalPending;
    private String status;
}
