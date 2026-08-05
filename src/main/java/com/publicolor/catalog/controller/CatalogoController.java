package com.publicolor.catalog.controller;

import com.publicolor.catalog.dto.CatalogosResponse;
import com.publicolor.catalog.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping
    public ResponseEntity<CatalogosResponse> obtenerTodos() {
        return ResponseEntity.ok(catalogoService.obtenerTodos());
    }
}
