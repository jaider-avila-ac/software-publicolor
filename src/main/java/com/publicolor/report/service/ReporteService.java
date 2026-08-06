package com.publicolor.report.service;

import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.report.dto.ReporteResponse;
import com.publicolor.report.dto.TipoReportePdf;

import java.time.LocalDate;

public interface ReporteService {
    ReporteResponse generar(Long clientId, EstadoCuenta status, LocalDate from, LocalDate to, TipoReportePdf movementType);
}
