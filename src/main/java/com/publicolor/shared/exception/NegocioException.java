package com.publicolor.shared.exception;

/** Excepción base para violaciones de reglas de negocio genéricas (400). */
public class NegocioException extends RuntimeException {
    public NegocioException(String message) {
        super(message);
    }
}
