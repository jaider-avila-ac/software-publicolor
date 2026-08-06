package com.publicolor.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Un movimiento individual (ingreso o egreso), listado uno por uno sin agrupar.
 * Los ingresos incluyen tanto los ingresos manuales como los pagos en efectivo a
 * trabajos (un pago SÍ cuenta como ingreso acá, igual que en el dashboard y el PDF).
 */
@Getter @Builder
public class ReporteConceptoItem {
    private LocalDate date;
    private String type; // INCOME | EXPENSE
    private String code;
    private String concept;
    private String category;
    private String clientName; // solo presente cuando el movimiento es un pago a trabajo
    private String jobCode;    // solo presente cuando el movimiento es un pago a trabajo
    private BigDecimal amount;
}
