package com.publicolor.finance.income.dto;

import com.publicolor.catalog.dto.LookupItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Fila unificada para la pantalla de Ingresos: junta los ingresos manuales con los
 * abonos a trabajos (que también son plata que entró), ordenados por fecha.
 */
@Getter @Builder
public class IngresoUnificadoResponse {
    private Long id;
    private String code;
    private String concept;
    private BigDecimal amount;
    private LocalDate date;
    private LookupItem category;
    private String notes;
    private LocalDateTime createdAt;
    private boolean annulled;
    private LocalDateTime annulledAt;
    private String annulledReason;
    /** MANUAL: ingreso cargado a mano. PAGO: abono de un trabajo. */
    private String source;
    /** Solo para source=PAGO: a qué trabajo corresponde el abono. */
    private String jobCode;
}
