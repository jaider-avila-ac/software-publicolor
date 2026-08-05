package com.publicolor.shared.exception;

/** Se intentó borrar definitivamente un trabajo que ya tiene pagos registrados (409). */
public class MovimientosFinancierosException extends RuntimeException {
    public MovimientosFinancierosException(String message) {
        super(message);
    }
}
