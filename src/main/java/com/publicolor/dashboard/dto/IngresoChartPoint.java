package com.publicolor.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/** Un punto del gráfico de ingresos: "2026-08-06" (día) o "2026-08" (mes) según la granularidad pedida. */
@Getter @Builder
public class IngresoChartPoint {
    private String period;
    private BigDecimal amount;
}
