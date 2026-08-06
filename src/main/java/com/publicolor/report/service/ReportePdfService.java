package com.publicolor.report.service;

import com.publicolor.report.dto.TipoReportePdf;

import java.time.LocalDate;

public interface ReportePdfService {
    byte[] generarPdf(TipoReportePdf tipo, LocalDate from, LocalDate to);
}
