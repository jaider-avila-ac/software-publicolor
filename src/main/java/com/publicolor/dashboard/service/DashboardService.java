package com.publicolor.dashboard.service;

import com.publicolor.dashboard.dto.DashboardResponse;
import com.publicolor.dashboard.dto.IngresoChartPoint;

import java.util.List;

public interface DashboardService {
    DashboardResponse obtener();

    /**
     * Ingresos (pagos en efectivo + ingresos manuales) agrupados por día o por mes.
     * "day": del 1 del mes actual hasta hoy. "month": de enero hasta el mes actual, del año en curso.
     * Nunca incluye fechas futuras.
     */
    List<IngresoChartPoint> obtenerGraficoIngresos(String granularity);
}
