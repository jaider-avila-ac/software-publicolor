package com.publicolor.catalog.controller;

import com.publicolor.catalog.dto.CatalogoItemAdminResponse;
import com.publicolor.catalog.dto.CatalogoItemRequest;
import com.publicolor.catalog.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Administración de los 6 catálogos: crear, editar y activar/desactivar. */
@RestController
@RequestMapping("/api/v1/catalogs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CatalogoAdminController {

    private final CatalogoService catalogoService;

    @GetMapping("/product-types")
    public ResponseEntity<List<CatalogoItemAdminResponse>> listarTiposProducto() {
        return ResponseEntity.ok(catalogoService.listarTiposProducto());
    }

    @PostMapping("/product-types")
    public ResponseEntity<CatalogoItemAdminResponse> crearTipoProducto(@Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearTipoProducto(req));
    }

    @PutMapping("/product-types/{id}")
    public ResponseEntity<CatalogoItemAdminResponse> actualizarTipoProducto(
            @PathVariable Long id, @Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.ok(catalogoService.actualizarTipoProducto(id, req));
    }

    @GetMapping("/finishes")
    public ResponseEntity<List<CatalogoItemAdminResponse>> listarAcabados() {
        return ResponseEntity.ok(catalogoService.listarAcabados());
    }

    @PostMapping("/finishes")
    public ResponseEntity<CatalogoItemAdminResponse> crearAcabado(@Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearAcabado(req));
    }

    @PutMapping("/finishes/{id}")
    public ResponseEntity<CatalogoItemAdminResponse> actualizarAcabado(
            @PathVariable Long id, @Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.ok(catalogoService.actualizarAcabado(id, req));
    }

    @GetMapping("/laminations")
    public ResponseEntity<List<CatalogoItemAdminResponse>> listarLaminados() {
        return ResponseEntity.ok(catalogoService.listarLaminados());
    }

    @PostMapping("/laminations")
    public ResponseEntity<CatalogoItemAdminResponse> crearLaminado(@Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearLaminado(req));
    }

    @PutMapping("/laminations/{id}")
    public ResponseEntity<CatalogoItemAdminResponse> actualizarLaminado(
            @PathVariable Long id, @Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.ok(catalogoService.actualizarLaminado(id, req));
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<CatalogoItemAdminResponse>> listarMetodosPago() {
        return ResponseEntity.ok(catalogoService.listarMetodosPago());
    }

    @PostMapping("/payment-methods")
    public ResponseEntity<CatalogoItemAdminResponse> crearMetodoPago(@Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearMetodoPago(req));
    }

    @PutMapping("/payment-methods/{id}")
    public ResponseEntity<CatalogoItemAdminResponse> actualizarMetodoPago(
            @PathVariable Long id, @Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.ok(catalogoService.actualizarMetodoPago(id, req));
    }

    @GetMapping("/income-categories")
    public ResponseEntity<List<CatalogoItemAdminResponse>> listarCategoriasIngreso() {
        return ResponseEntity.ok(catalogoService.listarCategoriasIngreso());
    }

    @PostMapping("/income-categories")
    public ResponseEntity<CatalogoItemAdminResponse> crearCategoriaIngreso(@Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearCategoriaIngreso(req));
    }

    @PutMapping("/income-categories/{id}")
    public ResponseEntity<CatalogoItemAdminResponse> actualizarCategoriaIngreso(
            @PathVariable Long id, @Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.ok(catalogoService.actualizarCategoriaIngreso(id, req));
    }

    @GetMapping("/expense-categories")
    public ResponseEntity<List<CatalogoItemAdminResponse>> listarCategoriasEgreso() {
        return ResponseEntity.ok(catalogoService.listarCategoriasEgreso());
    }

    @PostMapping("/expense-categories")
    public ResponseEntity<CatalogoItemAdminResponse> crearCategoriaEgreso(@Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearCategoriaEgreso(req));
    }

    @PutMapping("/expense-categories/{id}")
    public ResponseEntity<CatalogoItemAdminResponse> actualizarCategoriaEgreso(
            @PathVariable Long id, @Valid @RequestBody CatalogoItemRequest req) {
        return ResponseEntity.ok(catalogoService.actualizarCategoriaEgreso(id, req));
    }
}
