package com.publicolor.report.service;

import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.report.dto.ReporteResponse;

import java.time.LocalDate;

public interface ReporteService {
    ReporteResponse generar(Long clientId, EstadoCuenta status, LocalDate from, LocalDate to);
}
