package com.publicolor.payment.controller;

import com.publicolor.payment.dto.PagoRequest;
import com.publicolor.payment.dto.PagoResponse;
import com.publicolor.payment.service.PagoService;
import com.publicolor.shared.dto.AnularRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/payments")
    public ResponseEntity<PagoResponse> registrar(@Valid @RequestBody PagoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrar(req));
    }

    @GetMapping("/jobs/{jobId}/payments")
    public ResponseEntity<List<PagoResponse>> listarPorTrabajo(@PathVariable Long jobId) {
        return ResponseEntity.ok(pagoService.listarPorTrabajo(jobId));
    }

    @PostMapping("/payments/{id}/annul")
    public ResponseEntity<PagoResponse> anular(@PathVariable Long id, @RequestBody(required = false) AnularRequest req) {
        String reason = req == null ? null : req.getReason();
        return ResponseEntity.ok(pagoService.anular(id, reason));
    }
}
