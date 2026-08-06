package com.publicolor.finance.expense.service.impl;

import com.publicolor.catalog.dto.LookupItem;
import com.publicolor.catalog.model.CategoriaEgreso;
import com.publicolor.catalog.repository.CategoriaEgresoRepository;
import com.publicolor.finance.expense.dto.EgresoRequest;
import com.publicolor.finance.expense.dto.EgresoResponse;
import com.publicolor.finance.expense.model.Egreso;
import com.publicolor.finance.expense.repository.EgresoRepository;
import com.publicolor.finance.expense.repository.EgresoSpecifications;
import com.publicolor.finance.expense.service.EgresoService;
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
public class EgresoServiceImpl implements EgresoService {

    private final EgresoRepository egresoRepo;
    private final CategoriaEgresoRepository categoriaRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<EgresoResponse> listar(Long categoryId, LocalDate from, LocalDate to, Pageable pageable) {
        return egresoRepo.findAll(EgresoSpecifications.conFiltros(categoryId, from, to), pageable)
                .map(this::toResponse);
    }

    @Override
    public EgresoResponse crear(EgresoRequest req) {
        CategoriaEgreso categoria = null;
        if (req.getExpenseCategoryId() != null) {
            categoria = categoriaRepo.findById(req.getExpenseCategoryId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría de egreso no encontrada."));
        }

        Egreso egreso = Egreso.builder()
                .code(generarCodigoUnico())
                .categoria(categoria)
                .concept(req.getConcept().trim())
                .amount(req.getAmount())
                .expenseDate(req.getExpenseDate())
                .notes(req.getNotes())
                .build();

        return toResponse(egresoRepo.save(egreso));
    }

    /** Código único (ej. "EG-0001"); igual que en trabajos, se verifica explícitamente antes de usarlo. */
    private String generarCodigoUnico() {
        String codigo = "EG-" + String.format("%04d", egresoRepo.siguienteConsecutivo());
        if (egresoRepo.existsByCode(codigo)) {
            throw new com.publicolor.shared.exception.NegocioException("No se pudo generar un código único para el egreso. Intentá de nuevo.");
        }
        return codigo;
    }

    @Override
    public EgresoResponse anular(Long id, String reason) {
        Egreso egreso = egresoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Egreso no encontrado."));

        if (egreso.isAnnulled()) {
            throw new com.publicolor.shared.exception.NegocioException("Este egreso ya está anulado.");
        }

        egreso.setAnnulled(true);
        egreso.setAnnulledAt(com.publicolor.shared.util.TimeUtil.now());
        egreso.setAnnulledReason(reason);
        return toResponse(egresoRepo.save(egreso));
    }

    private EgresoResponse toResponse(Egreso e) {
        return EgresoResponse.builder()
                .id(e.getId())
                .code(e.getCode())
                .concept(e.getConcept())
                .amount(e.getAmount())
                .expenseDate(e.getExpenseDate())
                .category(e.getCategoria() == null ? null :
                        LookupItem.builder().id(e.getCategoria().getId()).name(e.getCategoria().getName()).build())
                .notes(e.getNotes())
                .createdAt(e.getCreatedAt())
                .annulled(e.isAnnulled())
                .annulledAt(e.getAnnulledAt())
                .annulledReason(e.getAnnulledReason())
                .build();
    }
}
