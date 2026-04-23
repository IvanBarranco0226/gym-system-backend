package com.gymsystem.api.exception;

// Esta es una excepción personalizada para errores de autenticación
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
