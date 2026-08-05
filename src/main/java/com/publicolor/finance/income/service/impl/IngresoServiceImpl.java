package com.publicolor.finance.income.service.impl;

import com.publicolor.catalog.dto.LookupItem;
import com.publicolor.catalog.model.CategoriaIngreso;
import com.publicolor.catalog.repository.CategoriaIngresoRepository;
import com.publicolor.finance.income.dto.IngresoRequest;
import com.publicolor.finance.income.dto.IngresoResponse;
import com.publicolor.finance.income.model.IngresoManual;
import com.publicolor.finance.income.repository.IngresoManualRepository;
import com.publicolor.finance.income.repository.IngresoManualSpecifications;
import com.publicolor.finance.income.service.IngresoService;
import com.publicolor.shared.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class IngresoServiceImpl implements IngresoService {

    private final IngresoManualRepository ingresoRepo;
    private final CategoriaIngresoRepository categoriaRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<IngresoResponse> listar(Long categoryId, LocalDate from, LocalDate to, Pageable pageable) {
        return ingresoRepo.findAll(IngresoManualSpecifications.conFiltros(categoryId, from, to), pageable)
                .map(this::toResponse);
    }

    @Override
    public IngresoResponse crear(IngresoRequest req) {
        CategoriaIngreso categoria = null;
        if (req.getIncomeCategoryId() != null) {
            categoria = categoriaRepo.findById(req.getIncomeCategoryId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría de ingreso no encontrada."));
        }

        IngresoManual ingreso = IngresoManual.builder()
                .categoria(categoria)
                .concept(req.getConcept().trim())
                .amount(req.getAmount())
                .incomeDate(req.getIncomeDate())
                .notes(req.getNotes())
                .build();

        return toResponse(ingresoRepo.save(ingreso));
    }

    private IngresoResponse toResponse(IngresoManual i) {
        return IngresoResponse.builder()
                .id(i.getId())
                .concept(i.getConcept())
                .amount(i.getAmount())
                .incomeDate(i.getIncomeDate())
                .category(i.getCategoria() == null ? null :
                        LookupItem.builder().id(i.getCategoria().getId()).name(i.getCategoria().getName()).build())
                .notes(i.getNotes())
                .createdAt(i.getCreatedAt())
                .build();
    }
}
