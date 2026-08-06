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
import com.publicolor.payment.model.OrigenPago;
import com.publicolor.payment.model.Pago;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.shared.exception.ConfirmacionRequeridaException;
import com.publicolor.shared.exception.CuentaPendienteException;
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

    /** Un cliente solo puede tener una cuenta en uno de estos estados a la vez. */
    private static final List<EstadoCuenta> ESTADOS_PENDIENTES =
            List.of(EstadoCuenta.ABIERTA, EstadoCuenta.PARCIALMENTE_PAGADA);

    private static final java.util.Locale ES = java.util.Locale.forLanguageTag("es-CO");

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
        Cliente cliente = resolverCliente(req);

        trabajoRepo.findFirstByCliente_IdAndStatusIn(cliente.getId(), ESTADOS_PENDIENTES).ifPresent(existente -> {
            throw new CuentaPendienteException(
                    "Este cliente ya tiene una cuenta pendiente (" + existente.getDisplayTitle()
                            + "). Agregá el nuevo producto ahí en vez de crear un trabajo nuevo.",
                    existente.getId());
        });

        // Saldo a favor disponible del cliente (de trabajos anteriores pagados de más),
        // calculado ANTES de crear este trabajo para no contarlo con su propio total.
        BigDecimal creditoDisponible = calcularCreditoDisponible(cliente.getId());

        Long consecutivo = trabajoRepo.siguienteConsecutivo();
        String codigo = generarCodigoUnico(consecutivo);

        Trabajo trabajo = Trabajo.builder()
                .cliente(cliente)
                .consecutiveNumber(consecutivo)
                .code(codigo)
                .title(cliente.getName() + " - " + codigo)
                .totalAmount(req.getTotalAmount())
                .jobDate(req.getJobDate())
                .notes(req.getNotes())
                .status(EstadoCuenta.ABIERTA)
                .build();

        for (ConceptoTrabajoRequest itemReq : req.getItems()) {
            trabajo.getConceptos().add(construirConcepto(trabajo, itemReq));
        }

        Trabajo guardado = trabajoRepo.save(trabajo);
        aplicarCreditoAutomatico(guardado, creditoDisponible);
        return toResponse(guardado);
    }

    /**
     * Código del trabajo (letras + números, ej. "OT-0009") a partir del consecutivo atómico
     * de la secuencia — ya es único por construcción, pero igual se verifica explícitamente
     * contra la tabla antes de usarlo.
     */
    private String generarCodigoUnico(Long consecutivo) {
        String codigo = "OT-" + String.format("%04d", consecutivo);
        if (trabajoRepo.existsByCode(codigo)) {
            throw new NegocioException("No se pudo generar un código único para el trabajo. Intentá de nuevo.");
        }
        return codigo;
    }

    /** Código único para el pago de crédito auto-aplicado; mismo patrón que los demás pagos. */
    private String generarCodigoPagoUnico() {
        String codigo = "AB-" + String.format("%04d", pagoRepo.siguienteConsecutivo());
        if (pagoRepo.existsByCode(codigo)) {
            throw new NegocioException("No se pudo generar un código único para el pago. Intentá de nuevo.");
        }
        return codigo;
    }

    /** Saldo a favor real (solo plata efectivamente recibida) que el cliente tiene libre hoy. */
    private BigDecimal calcularCreditoDisponible(Long clienteId) {
        BigDecimal comprado = trabajoRepo.sumVendidoPorCliente(clienteId);
        BigDecimal pagadoEnEfectivo = pagoRepo.sumCashAmountByClientId(clienteId);
        BigDecimal saldo = pagadoEnEfectivo.subtract(comprado);
        return saldo.compareTo(BigDecimal.ZERO) > 0 ? saldo : BigDecimal.ZERO;
    }

    /**
     * Si el cliente tiene saldo a favor, se aplica solo, sin ningún paso manual:
     * se registra como un pago del nuevo trabajo (origen CREDIT_APPLIED, no es
     * plata nueva) por el menor entre el crédito disponible y el total del trabajo.
     */
    private void aplicarCreditoAutomatico(Trabajo trabajo, BigDecimal creditoDisponible) {
        if (creditoDisponible.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal aplicado = creditoDisponible.min(trabajo.getTotalAmount());
        if (aplicado.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        Pago pago = Pago.builder()
                .code(generarCodigoPagoUnico())
                .trabajo(trabajo)
                .amount(aplicado)
                .paymentDate(trabajo.getJobDate())
                .origin(OrigenPago.CREDIT_APPLIED)
                .notes("Aplicado automáticamente: saldo a favor del cliente")
                .build();
        pagoRepo.save(pago);
        recalcularEstado(trabajo);
    }

    @Override
    public TrabajoResponse actualizar(Long id, TrabajoUpdateRequest req) {
        Trabajo trabajo = obtenerEntidad(id);
        exigirEditable(trabajo);

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

    /**
     * Resuelve el cliente del trabajo: por id si viene, o buscando/creando por
     * nombre (el cliente solo requiere nombre, así que no hace falta un alta
     * previa separada para poder facturarle un trabajo).
     */
    private Cliente resolverCliente(TrabajoRequest req) {
        if (req.getClientId() != null) {
            return clienteRepo.findById(req.getClientId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));
        }
        if (req.getClientName() != null && !req.getClientName().isBlank()) {
            String normalizado = req.getClientName().trim().toUpperCase(ES);
            return clienteRepo.findByNameIgnoreCase(normalizado)
                    .orElseGet(() -> clienteRepo.save(Cliente.builder().name(normalizado).build()));
        }
        throw new NegocioException("El cliente es obligatorio.");
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

        Set<Acabado> acabados = resolverAcabados(req.getFinishIds());
        Set<Laminado> laminados = resolverLaminados(req.getLaminationIds());

        concepto.setTrabajo(trabajo);
        concepto.setTipoProducto(tipoProducto);
        concepto.setDescription(req.getDescription());
        concepto.setQuantity(req.getQuantity());
        concepto.setWidth(req.getWidth());
        concepto.setHeight(req.getHeight());
        concepto.setAcabados(acabados);
        concepto.setLaminados(laminados);
        concepto.setUnitPrice(req.getUnitPrice());
        concepto.setTotalAmount(req.getTotalAmount());
        concepto.setNotes(req.getNotes());
    }

    private Set<Acabado> resolverAcabados(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.LinkedHashSet<>();
        }
        List<Acabado> encontrados = acabadoRepo.findAllById(ids);
        if (encontrados.size() != new java.util.HashSet<>(ids).size()) {
            throw new RecursoNoEncontradoException("Alguno de los acabados seleccionados no existe.");
        }
        return new java.util.LinkedHashSet<>(encontrados);
    }

    private Set<Laminado> resolverLaminados(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.LinkedHashSet<>();
        }
        List<Laminado> encontrados = laminadoRepo.findAllById(ids);
        if (encontrados.size() != new java.util.HashSet<>(ids).size()) {
            throw new RecursoNoEncontradoException("Alguno de los laminados seleccionados no existe.");
        }
        return new java.util.LinkedHashSet<>(encontrados);
    }

    private TrabajoResponse toResponse(Trabajo t) {
        BigDecimal totalPagado = pagoRepo.sumAmountByTrabajoId(t.getId());
        BigDecimal pendiente = t.getTotalAmount().subtract(totalPagado);
        BigDecimal creditoAplicado = pagoRepo.sumCreditAppliedByTrabajoId(t.getId());

        List<ConceptoTrabajoResponse> items = t.getConceptos().stream().map(this::toResponse).toList();

        return TrabajoResponse.builder()
                .id(t.getId())
                .consecutiveNumber(t.getConsecutiveNumber())
                .code(t.getCode())
                .client(LookupItem.builder().id(t.getCliente().getId()).name(t.getCliente().getName()).build())
                .title(t.getDisplayTitle())
                .totalAmount(t.getTotalAmount())
                .totalPaid(totalPagado)
                .pendingAmount(pendiente)
                .creditApplied(creditoAplicado)
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
                .finishes(c.getAcabados().stream()
                        .map(a -> LookupItem.builder().id(a.getId()).name(a.getName()).build())
                        .toList())
                .laminations(c.getLaminados().stream()
                        .map(l -> LookupItem.builder().id(l.getId()).name(l.getName()).build())
                        .toList())
                .unitPrice(c.getUnitPrice())
                .totalAmount(c.getTotalAmount())
                .notes(c.getNotes())
                .build();
    }
}
