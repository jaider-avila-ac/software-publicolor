package com.publicolor.job.service;

import com.publicolor.job.dto.*;
import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.model.Trabajo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TrabajoService {

    Page<TrabajoResponse> listar(Long clientId, EstadoCuenta status, LocalDate from, LocalDate to, Pageable pageable);

    java.util.List<TrabajoResponse> listarPorCliente(Long clientId);

    java.util.List<TrabajoResponse> listarRecientes();

    TrabajoResponse obtener(Long id);

    TrabajoResponse crear(TrabajoRequest req);

    TrabajoResponse actualizar(Long id, TrabajoUpdateRequest req);

    ConceptoTrabajoResponse agregarConcepto(Long jobId, ConceptoTrabajoRequest req);

    ConceptoTrabajoResponse actualizarConcepto(Long jobId, Long itemId, ConceptoTrabajoRequest req);

    void eliminarConcepto(Long jobId, Long itemId);

    void cancelar(Long id, boolean force);

    void eliminar(Long id);

    /** Recalcula ABIERTA / PARCIALMENTE_PAGADA / PAGADA según lo pagado. No toca CANCELADA. */
    void recalcularEstado(Trabajo trabajo);

    /** Trabajo entidad para uso interno de otros servicios (ej. PagoService). */
    Trabajo obtenerEntidad(Long id);
}
