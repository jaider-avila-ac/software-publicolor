package com.publicolor.finance.income.service;

import com.publicolor.finance.income.dto.IngresoRequest;
import com.publicolor.finance.income.dto.IngresoResponse;
import com.publicolor.finance.income.dto.IngresoUnificadoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IngresoService {
    Page<IngresoResponse> listar(Long categoryId, LocalDate from, LocalDate to, Pageable pageable);
    IngresoResponse crear(IngresoRequest req);
    IngresoResponse anular(Long id, String reason);

    /** Ingresos manuales + abonos a trabajos, juntos y ordenados por fecha. */
    List<IngresoUnificadoResponse> listarUnificado(Long categoryId, LocalDate from, LocalDate to);
}
