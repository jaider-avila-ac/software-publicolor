package com.publicolor.finance.income.service;

import com.publicolor.finance.income.dto.IngresoRequest;
import com.publicolor.finance.income.dto.IngresoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IngresoService {
    Page<IngresoResponse> listar(Long categoryId, LocalDate from, LocalDate to, Pageable pageable);
    IngresoResponse crear(IngresoRequest req);
}
