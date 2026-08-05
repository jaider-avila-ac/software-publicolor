package com.publicolor.job.controller;

import com.publicolor.job.dto.*;
import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.service.TrabajoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TrabajoController {

    private final TrabajoService trabajoService;

    @GetMapping
    public ResponseEntity<Page<TrabajoResponse>> listar(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) EstadoCuenta status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(trabajoService.listar(clientId, status, from, to, pageable));
    }

    @PostMapping
    public ResponseEntity<TrabajoResponse> crear(@Valid @RequestBody TrabajoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trabajoService.crear(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(trabajoService.obtener(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrabajoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody TrabajoUpdateRequest req) {
        return ResponseEntity.ok(trabajoService.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        trabajoService.eliminar(id);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TrabajoResponse> cancelar(@PathVariable Long id, @RequestBody(required = false) CancelarTrabajoRequest req) {
        boolean force = req != null && req.isForce();
        trabajoService.cancelar(id, force);
        return ResponseEntity.ok(trabajoService.obtener(id));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ConceptoTrabajoResponse> agregarConcepto(
            @PathVariable Long id, @Valid @RequestBody ConceptoTrabajoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trabajoService.agregarConcepto(id, req));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<ConceptoTrabajoResponse> actualizarConcepto(
            @PathVariable Long id, @PathVariable Long itemId, @Valid @RequestBody ConceptoTrabajoRequest req) {
        return ResponseEntity.ok(trabajoService.actualizarConcepto(id, itemId, req));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarConcepto(@PathVariable Long id, @PathVariable Long itemId) {
        trabajoService.eliminarConcepto(id, itemId);
    }
}
