package com.publicolor.receipt.controller;

import com.publicolor.receipt.dto.ReciboResponse;
import com.publicolor.receipt.service.ReciboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jobs/{jobId}/receipts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReciboController {

    private final ReciboService reciboService;

    @PostMapping
    public ResponseEntity<ReciboResponse> generar(@PathVariable Long jobId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reciboService.generar(jobId));
    }
}
