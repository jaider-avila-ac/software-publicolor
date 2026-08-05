package com.publicolor.finance.expense.controller;

import com.publicolor.finance.expense.dto.EgresoRequest;
import com.publicolor.finance.expense.dto.EgresoResponse;
import com.publicolor.finance.expense.service.EgresoService;
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
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EgresoController {

    private final EgresoService egresoService;

    @GetMapping
    public ResponseEntity<Page<EgresoResponse>> listar(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "expenseDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(egresoService.listar(categoryId, from, to, pageable));
    }

    @PostMapping
    public ResponseEntity<EgresoResponse> crear(@Valid @RequestBody EgresoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(egresoService.crear(req));
    }
}
