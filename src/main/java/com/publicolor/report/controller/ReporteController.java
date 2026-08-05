package com.publicolor.report.controller;

import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.report.dto.ReporteResponse;
import com.publicolor.report.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping
    public ResponseEntity<ReporteResponse> generar(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) EstadoCuenta status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reporteService.generar(clientId, status, from, to));
    }
}
