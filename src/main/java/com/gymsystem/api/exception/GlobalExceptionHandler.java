package com.gymsystem.api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Esta etiqueta convierte a esta clase en el paracaídas global de toda tu API
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Atrapa los errores de validación (Cuando falla el @valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors() ){

            errors.put(error.getField(), error.getDefaultMessage());
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Error en los datos enviados", errors);
    }

    // 2. Atrapa errores de autenticación (Login fallido)
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    // 3. Atrapa nuestros propios RuntimeExceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // 4. Atrapa cualquier otro error catastrófico (falla BD, NullPointer, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalExceptions(Exception ex) {
        // Logueamos el error REAL en la consola del servidor (privado)
        log.error("ERROR NO CONTROLADO: ", ex);
        
        // Al frontend le mandamos un mensaje genérico (seguridad)
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor.", null);
    }

    // Método auxiliar para que todos los errores tenga el mismo formato JSON
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, Object details) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("message", message);
        if (details != null) {
            response.put("details", details);
        }
        return new ResponseEntity<>(response, status);

    }

}