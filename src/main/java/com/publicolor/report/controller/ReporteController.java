package com.publicolor.report.controller;

import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.report.dto.ReporteResponse;
import com.publicolor.report.dto.TipoReportePdf;
import com.publicolor.report.service.ReportePdfService;
import com.publicolor.report.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    private final ReporteService reporteService;
    private final ReportePdfService reportePdfService;

    @GetMapping
    public ResponseEntity<ReporteResponse> generar(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) EstadoCuenta status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "BOTH") TipoReportePdf type) {
        return ResponseEntity.ok(reporteService.generar(clientId, status, from, to, type));
    }

    /** Tabla simple de ingresos y/o egresos en un rango de fechas, lista para imprimir. */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam TipoReportePdf type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        byte[] pdf = reportePdfService.generarPdf(type, from, to);

        String nombreArchivo = "publicolor-" + type.name().toLowerCase() + "-" + from + "_" + to + ".pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(nombreArchivo).build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
