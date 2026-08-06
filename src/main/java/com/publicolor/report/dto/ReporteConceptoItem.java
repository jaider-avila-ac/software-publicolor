package com.publicolor.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Un concepto (producto) individual vendido — no agrupado, para listar uno por uno. */
@Getter @Builder
public class ReporteConceptoItem {
    private Long jobId;
    private String jobCode;
    private String clientName;
    private LocalDate jobDate;
    private String productType;
    private String description;
    private BigDecimal amount;
}
