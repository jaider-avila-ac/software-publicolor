package com.publicolor.receipt.controller;

import com.publicolor.receipt.dto.ReciboPdfRequest;
import com.publicolor.receipt.dto.ReciboResponse;
import com.publicolor.receipt.service.ReciboPdfService;
import com.publicolor.receipt.service.ReciboService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jobs/{jobId}/receipts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReciboController {

    private final ReciboService reciboService;
    private final ReciboPdfService reciboPdfService;

    @PostMapping
    public ResponseEntity<ReciboResponse> generar(@PathVariable Long jobId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reciboService.generar(jobId));
    }

    /**
     * No crea un recibo nuevo ni consume otro consecutivo: renderiza en PDF el mismo
     * recibo que el frontend ya generó y tiene en pantalla.
     */
    @PostMapping("/pdf")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Long jobId, @Valid @RequestBody ReciboPdfRequest req) {
        byte[] pdf = reciboPdfService.generarPdf(req);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("recibo-publicolor-" + req.getConsecutiveNumber() + ".pdf").build());
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdf);
    }
}
