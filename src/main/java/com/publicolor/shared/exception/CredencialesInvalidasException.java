package com.publicolor.shared.exception;

/** Correo o contraseña incorrectos al iniciar sesión (401). */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
