package com.publicolor.shared.exception;

/** Un cliente, trabajo, pago u otro recurso solicitado no existe (404). */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
