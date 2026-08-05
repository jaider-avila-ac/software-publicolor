package com.publicolor.finance.expense.service;

import com.publicolor.finance.expense.dto.EgresoRequest;
import com.publicolor.finance.expense.dto.EgresoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface EgresoService {
    Page<EgresoResponse> listar(Long categoryId, LocalDate from, LocalDate to, Pageable pageable);
    EgresoResponse crear(EgresoRequest req);
}
