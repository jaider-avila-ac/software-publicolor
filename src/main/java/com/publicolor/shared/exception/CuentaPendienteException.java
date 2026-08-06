package com.publicolor.shared.exception;

import lombok.Getter;

/**
 * Un cliente solo puede tener una cuenta pendiente (ABIERTA o PARCIALMENTE_PAGADA)
 * a la vez — se libera cuando esa cuenta llega a PAGADA (o se cancela). Se lanza al
 * intentar crear un trabajo nuevo para un cliente que ya tiene una cuenta pendiente.
 */
@Getter
public class CuentaPendienteException extends RuntimeException {

    private final Long existingJobId;

    public CuentaPendienteException(String message, Long existingJobId) {
        super(message);
        this.existingJobId = existingJobId;
    }
}
