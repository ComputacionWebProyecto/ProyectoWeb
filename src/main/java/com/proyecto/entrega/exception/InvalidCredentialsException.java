package com.proyecto.entrega.exception;

/**
 * Excepción para indicar que las credenciales de autenticación son inválidas.
 * Se mapea a HTTP 401 (Unauthorized).
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}