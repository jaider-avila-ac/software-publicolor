package com.publicolor.finance.income.controller;

import com.publicolor.finance.income.dto.IngresoRequest;
import com.publicolor.finance.income.dto.IngresoResponse;
import com.publicolor.finance.income.service.IngresoService;
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
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class IngresoController {

    private final IngresoService ingresoService;

    @GetMapping
    public ResponseEntity<Page<IngresoResponse>> listar(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "incomeDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ingresoService.listar(categoryId, from, to, pageable));
    }

    @PostMapping
    public ResponseEntity<IngresoResponse> crear(@Valid @RequestBody IngresoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingresoService.crear(req));
    }
}
