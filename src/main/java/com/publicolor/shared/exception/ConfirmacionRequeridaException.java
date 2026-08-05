package com.publicolor.shared.exception;

/**
 * Una acción requiere confirmación explícita del usuario antes de proceder
 * (sobrepago de una cuenta, cancelar una cuenta con pagos registrados). El
 * frontend debe mostrar un diálogo de confirmación y reintentar con la
 * bandera "force" correspondiente en la request (409).
 */
public class ConfirmacionRequeridaException extends RuntimeException {
    public ConfirmacionRequeridaException(String message) {
        super(message);
    }
}
