package com.publicolor.job.service.impl;

import com.publicolor.catalog.dto.LookupItem;
import com.publicolor.catalog.model.Acabado;
import com.publicolor.catalog.model.Laminado;
import com.publicolor.catalog.model.TipoProducto;
import com.publicolor.catalog.repository.AcabadoRepository;
import com.publicolor.catalog.repository.LaminadoRepository;
import com.publicolor.catalog.repository.TipoProductoRepository;
import com.publicolor.client.model.Cliente;
import com.publicolor.client.repository.ClienteRepository;
import com.publicolor.job.dto.*;
import com.publicolor.job.model.ConceptoTrabajo;
import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.model.Trabajo;
import com.publicolor.job.repository.ConceptoTrabajoRepository;
import com.publicolor.job.repository.TrabajoRepository;
import com.publicolor.job.repository.TrabajoSpecifications;
import com.publicolor.job.service.TrabajoService;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.shared.exception.ConfirmacionRequeridaException;
import com.publicolor.shared.exception.MovimientosFinancierosException;
import com.publicolor.shared.exception.NegocioException;
import com.publicolor.shared.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TrabajoServiceImpl implements TrabajoService {

    private static final Set<EstadoCuenta> ESTADOS_EDITABLES =
            Set.of(EstadoCuenta.ABIERTA, EstadoCuenta.PARCIALMENTE_PAGADA);

    private final TrabajoRepository trabajoRepo;
    private final ConceptoTrabajoRepository conceptoRepo;
    private final ClienteRepository clienteRepo;
    private final TipoProductoRepository tipoProductoRepo;
    private final AcabadoRepository acabadoRepo;
    private final LaminadoRepository laminadoRepo;
    private final PagoRepository pagoRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<TrabajoResponse> listar(Long clientId, EstadoCuenta status, LocalDate from, LocalDate to, Pageable pageable) {
        return trabajoRepo.findAll(TrabajoSpecifications.conFiltros(clientId, status, from, to), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajoResponse obtener(Long id) {
        return toResponse(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajoResponse> listarPorCliente(Long clientId) {
        return trabajoRepo.findByCliente_IdOrderByJobDateDesc(clientId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajoResponse> listarRecientes() {
        return trabajoRepo.findTop10ByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Override
    public Trabajo obtenerEntidad(Long id) {
        return trabajoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trabajo no encontrado."));
    }

    @Override
    public TrabajoResponse crear(TrabajoRequest req) {
        Cliente cliente = clienteRepo.findById(req.getClientId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));

        Trabajo trabajo = Trabajo.builder()
                .cliente(cliente)
                .consecutiveNumber(trabajoRepo.siguienteConsecutivo())
                .title(req.getTitle())
                .totalAmount(req.getTotalAmount())
                .jobDate(req.getJobDate())
                .notes(req.getNotes())
                .status(EstadoCuenta.ABIERTA)
                .build();

        for (ConceptoTrabajoRequest itemReq : req.getItems()) {
            trabajo.getConceptos().add(construirConcepto(trabajo, itemReq));
        }

        Trabajo guardado = trabajoRepo.save(trabajo);
        return toResponse(guardado);
    }

    @Override
    public TrabajoResponse actualizar(Long id, TrabajoUpdateRequest req) {
        Trabajo trabajo = obtenerEntidad(id);
        exigirEditable(trabajo);

        trabajo.setTitle(req.getTitle());
        trabajo.setJobDate(req.getJobDate());
        trabajo.setTotalAmount(req.getTotalAmount());
        trabajo.setNotes(req.getNotes());

        Trabajo guardado = trabajoRepo.save(trabajo);
        recalcularEstado(guardado);
        return toResponse(guardado);
    }

    @Override
    public ConceptoTrabajoResponse agregarConcepto(Long jobId, ConceptoTrabajoRequest req) {
        Trabajo trabajo = obtenerEntidad(jobId);
        exigirEditable(trabajo);

        ConceptoTrabajo concepto = construirConcepto(trabajo, req);
        trabajo.getConceptos().add(concepto);
        trabajoRepo.save(trabajo);
        return toResponse(concepto);
    }

    @Override
    public ConceptoTrabajoResponse actualizarConcepto(Long jobId, Long itemId, ConceptoTrabajoRequest req) {
        Trabajo trabajo = obtenerEntidad(jobId);
        exigirEditable(trabajo);

        ConceptoTrabajo concepto = conceptoRepo.findById(itemId)
                .filter(c -> c.getTrabajo().getId().equals(jobId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Concepto no encontrado en este trabajo."));

        aplicarDatosConcepto(concepto, trabajo, req);
        conceptoRepo.save(concepto);
        return toResponse(concepto);
    }

    @Override
    public void eliminarConcepto(Long jobId, Long itemId) {
        Trabajo trabajo = obtenerEntidad(jobId);
        exigirEditable(trabajo);

        ConceptoTrabajo concepto = conceptoRepo.findById(itemId)
                .filter(c -> c.getTrabajo().getId().equals(jobId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Concepto no encontrado en este trabajo."));

        if (trabajo.getConceptos().size() <= 1) {
            throw new NegocioException("El trabajo debe conservar al menos un concepto.");
        }

        trabajo.getConceptos().remove(concepto);
        conceptoRepo.delete(concepto);
    }

    @Override
    public void cancelar(Long id, boolean force) {
        Trabajo trabajo = obtenerEntidad(id);
        BigDecimal totalPagado = pagoRepo.sumAmountByTrabajoId(id);

        if (totalPagado.compareTo(BigDecimal.ZERO) > 0 && !force) {
            throw new ConfirmacionRequeridaException(
                    "Este trabajo ya tiene pagos registrados. Confirmá para cancelarlo de todas formas.");
        }

        trabajo.setStatus(EstadoCuenta.CANCELADA);
        trabajoRepo.save(trabajo);
    }

    @Override
    public void eliminar(Long id) {
        Trabajo trabajo = obtenerEntidad(id);
        BigDecimal totalPagado = pagoRepo.sumAmountByTrabajoId(id);

        if (totalPagado.compareTo(BigDecimal.ZERO) > 0) {
            throw new MovimientosFinancierosException(
                    "Este trabajo tiene pagos registrados; no se puede borrar. Cancélalo en su lugar.");
        }

        trabajoRepo.delete(trabajo);
    }

    @Override
    public void recalcularEstado(Trabajo trabajo) {
        if (trabajo.getStatus() == EstadoCuenta.CANCELADA) {
            return;
        }
        BigDecimal totalPagado = pagoRepo.sumAmountByTrabajoId(trabajo.getId());
        if (totalPagado.compareTo(BigDecimal.ZERO) <= 0) {
            trabajo.setStatus(EstadoCuenta.ABIERTA);
        } else if (totalPagado.compareTo(trabajo.getTotalAmount()) < 0) {
            trabajo.setStatus(EstadoCuenta.PARCIALMENTE_PAGADA);
        } else {
            trabajo.setStatus(EstadoCuenta.PAGADA);
        }
        trabajoRepo.save(trabajo);
    }

    private void exigirEditable(Trabajo trabajo) {
        if (!ESTADOS_EDITABLES.contains(trabajo.getStatus())) {
            throw new NegocioException("Este trabajo ya no se puede editar en su estado actual.");
        }
    }

    private ConceptoTrabajo construirConcepto(Trabajo trabajo, ConceptoTrabajoRequest req) {
        ConceptoTrabajo concepto = ConceptoTrabajo.builder().trabajo(trabajo).build();
        aplicarDatosConcepto(concepto, trabajo, req);
        return concepto;
    }

    private void aplicarDatosConcepto(ConceptoTrabajo concepto, Trabajo trabajo, ConceptoTrabajoRequest req) {
        TipoProducto tipoProducto = tipoProductoRepo.findById(req.getProductTypeId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de producto no encontrado."));

        Acabado acabado = null;
        if (req.getFinishId() != null) {
            acabado = acabadoRepo.findById(req.getFinishId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Acabado no encontrado."));
        }

        Laminado laminado = null;
        if (req.getLaminationId() != null) {
            laminado = laminadoRepo.findById(req.getLaminationId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Laminado no encontrado."));
        }

        concepto.setTrabajo(trabajo);
        concepto.setTipoProducto(tipoProducto);
        concepto.setDescription(req.getDescription());
        concepto.setQuantity(req.getQuantity());
        concepto.setWidth(req.getWidth());
        concepto.setHeight(req.getHeight());
        concepto.setAcabado(acabado);
        concepto.setLaminado(laminado);
        concepto.setUnitPrice(req.getUnitPrice());
        concepto.setTotalAmount(req.getTotalAmount());
        concepto.setNotes(req.getNotes());
    }

    private TrabajoResponse toResponse(Trabajo t) {
        BigDecimal totalPagado = pagoRepo.sumAmountByTrabajoId(t.getId());
        BigDecimal pendiente = t.getTotalAmount().subtract(totalPagado);

        List<ConceptoTrabajoResponse> items = t.getConceptos().stream().map(this::toResponse).toList();

        return TrabajoResponse.builder()
                .id(t.getId())
                .consecutiveNumber(t.getConsecutiveNumber())
                .client(LookupItem.builder().id(t.getCliente().getId()).name(t.getCliente().getName()).build())
                .title(t.getTitle())
                .totalAmount(t.getTotalAmount())
                .totalPaid(totalPagado)
                .pendingAmount(pendiente)
                .status(t.getStatus().name())
                .notes(t.getNotes())
                .jobDate(t.getJobDate())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .items(items)
                .build();
    }

    private ConceptoTrabajoResponse toResponse(ConceptoTrabajo c) {
        return ConceptoTrabajoResponse.builder()
                .id(c.getId())
                .productType(LookupItem.builder().id(c.getTipoProducto().getId()).name(c.getTipoProducto().getName()).build())
                .description(c.getDescription())
                .quantity(c.getQuantity())
                .width(c.getWidth())
                .height(c.getHeight())
                .finish(c.getAcabado() == null ? null :
                        LookupItem.builder().id(c.getAcabado().getId()).name(c.getAcabado().getName()).build())
                .lamination(c.getLaminado() == null ? null :
                        LookupItem.builder().id(c.getLaminado().getId()).name(c.getLaminado().getName()).build())
                .unitPrice(c.getUnitPrice())
                .totalAmount(c.getTotalAmount())
                .notes(c.getNotes())
                .build();
    }
}
