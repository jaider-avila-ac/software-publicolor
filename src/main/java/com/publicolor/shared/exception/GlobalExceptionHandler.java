package com.publicolor.shared.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/** Traduce toda excepción a un cuerpo JSON uniforme {"message": "..."}. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(CuentaCanceladaException.class)
    public ResponseEntity<Map<String, String>> handleCuentaCancelada(CuentaCanceladaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ConfirmacionRequeridaException.class)
    public ResponseEntity<Map<String, String>> handleConfirmacionRequerida(ConfirmacionRequeridaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage(), "code", "CONFIRMATION_REQUIRED"));
    }

    @ExceptionHandler(MovimientosFinancierosException.class)
    public ResponseEntity<Map<String, String>> handleMovimientosFinancieros(MovimientosFinancierosException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(CuentaPendienteException.class)
    public ResponseEntity<Map<String, String>> handleCuentaPendiente(CuentaPendienteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "message", ex.getMessage(),
                "code", "PENDING_ACCOUNT_EXISTS",
                "existingJobId", String.valueOf(ex.getExistingJobId())));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, String>> handleNegocio(NegocioException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    /** Excepciones técnicas lanzadas explícitamente (parámetros, auth) — pasan tal cual. */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        String msg = ex.getReason() != null ? ex.getReason() : "Error";
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", msg));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("message", "Método " + ex.getMethod() + " no permitido."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "El parámetro '" + ex.getParameterName() + "' es requerido."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", "Parámetro inválido: " + ex.getName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", "El cuerpo de la petición no es válido."));
    }

    /** Errores de validación @Valid en @RequestBody */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Datos inválidos.");
        return ResponseEntity.badRequest().body(Map.of("message", msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(cv -> cv.getMessage())
                .findFirst()
                .orElse("Parámetro inválido.");
        return ResponseEntity.badRequest().body(Map.of("message", msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    /** Cualquier otra excepción — se registra en el log, nunca se expone el detalle al cliente. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        log.error("Error interno no manejado: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(Map.of("message", "Error interno del servidor."));
    }
}
