package com.publicolor.shared.exception;

/** Se intentó registrar un pago sobre un trabajo con estado CANCELADA (409). */
public class CuentaCanceladaException extends RuntimeException {
    public CuentaCanceladaException(String message) {
        super(message);
    }
}
