package com.publicolor.finance.income.service.impl;

import com.publicolor.catalog.dto.LookupItem;
import com.publicolor.catalog.model.CategoriaIngreso;
import com.publicolor.catalog.repository.CategoriaIngresoRepository;
import com.publicolor.finance.income.dto.IngresoRequest;
import com.publicolor.finance.income.dto.IngresoResponse;
import com.publicolor.finance.income.dto.IngresoUnificadoResponse;
import com.publicolor.finance.income.model.IngresoManual;
import com.publicolor.finance.income.repository.IngresoManualRepository;
import com.publicolor.finance.income.repository.IngresoManualSpecifications;
import com.publicolor.finance.income.service.IngresoService;
import com.publicolor.payment.model.OrigenPago;
import com.publicolor.payment.model.Pago;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.shared.exception.RecursoNoEncontradoException;
import com.publicolor.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IngresoServiceImpl implements IngresoService {

    private static final LocalDate DESDE_SIEMPRE = LocalDate.of(2000, 1, 1);

    private final IngresoManualRepository ingresoRepo;
    private final CategoriaIngresoRepository categoriaRepo;
    private final PagoRepository pagoRepo;

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
                .code(generarCodigoUnico())
                .categoria(categoria)
                .concept(req.getConcept().trim())
                .amount(req.getAmount())
                .incomeDate(req.getIncomeDate())
                .notes(req.getNotes())
                .build();

        return toResponse(ingresoRepo.save(ingreso));
    }

    /** Código único (ej. "IN-0001"); igual que en trabajos, se verifica explícitamente antes de usarlo. */
    private String generarCodigoUnico() {
        String codigo = "IN-" + String.format("%04d", ingresoRepo.siguienteConsecutivo());
        if (ingresoRepo.existsByCode(codigo)) {
            throw new com.publicolor.shared.exception.NegocioException("No se pudo generar un código único para el ingreso. Intentá de nuevo.");
        }
        return codigo;
    }

    @Override
    public IngresoResponse anular(Long id, String reason) {
        IngresoManual ingreso = ingresoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ingreso no encontrado."));

        if (ingreso.isAnnulled()) {
            throw new com.publicolor.shared.exception.NegocioException("Este ingreso ya está anulado.");
        }

        ingreso.setAnnulled(true);
        ingreso.setAnnulledAt(com.publicolor.shared.util.TimeUtil.now());
        ingreso.setAnnulledReason(reason);
        return toResponse(ingresoRepo.save(ingreso));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoUnificadoResponse> listarUnificado(Long categoryId, LocalDate from, LocalDate to) {
        LocalDate desde = from != null ? from : DESDE_SIEMPRE;
        LocalDate hasta = to != null ? to : TimeUtil.today();

        List<IngresoUnificadoResponse> resultado = new ArrayList<>();

        for (IngresoManual i : ingresoRepo.findAll(IngresoManualSpecifications.conFiltros(categoryId, desde, hasta),
                Sort.by(Sort.Direction.DESC, "incomeDate"))) {
            resultado.add(IngresoUnificadoResponse.builder()
                    .id(i.getId())
                    .code(i.getCode())
                    .concept(i.getConcept())
                    .amount(i.getAmount())
                    .date(i.getIncomeDate())
                    .category(i.getCategoria() == null ? null :
                            LookupItem.builder().id(i.getCategoria().getId()).name(i.getCategoria().getName()).build())
                    .notes(i.getNotes())
                    .createdAt(i.getCreatedAt())
                    .annulled(i.isAnnulled())
                    .annulledAt(i.getAnnulledAt())
                    .annulledReason(i.getAnnulledReason())
                    .source("MANUAL")
                    .build());
        }

        // Los abonos a trabajos no tienen categoría de ingreso, así que solo entran cuando no se filtra por una específica.
        if (categoryId == null) {
            for (Pago p : pagoRepo.findByPaymentDateBetweenAndOriginOrderByPaymentDateDesc(desde, hasta, OrigenPago.CASH)) {
                resultado.add(IngresoUnificadoResponse.builder()
                        .id(p.getId())
                        .code(p.getCode())
                        .concept("Pago — " + p.getTrabajo().getCliente().getName())
                        .amount(p.getAmount())
                        .date(p.getPaymentDate())
                        .category(null)
                        .notes(p.getNotes())
                        .createdAt(p.getCreatedAt())
                        .annulled(p.isAnnulled())
                        .annulledAt(p.getAnnulledAt())
                        .annulledReason(p.getAnnulledReason())
                        .source("PAGO")
                        .jobCode(p.getTrabajo().getCode())
                        .build());
            }
        }

        resultado.sort(Comparator.comparing(IngresoUnificadoResponse::getDate).reversed());
        return resultado;
    }

    private IngresoResponse toResponse(IngresoManual i) {
        return IngresoResponse.builder()
                .id(i.getId())
                .code(i.getCode())
                .concept(i.getConcept())
                .amount(i.getAmount())
                .incomeDate(i.getIncomeDate())
                .category(i.getCategoria() == null ? null :
                        LookupItem.builder().id(i.getCategoria().getId()).name(i.getCategoria().getName()).build())
                .notes(i.getNotes())
                .createdAt(i.getCreatedAt())
                .annulled(i.isAnnulled())
                .annulledAt(i.getAnnulledAt())
                .annulledReason(i.getAnnulledReason())
                .build();
    }
}
