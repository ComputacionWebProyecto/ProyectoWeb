package com.proyecto.entrega.exception;

/**
 * Excepción para indicar que un usuario autenticado no tiene permisos
 * para realizar una acción específica.
 * Mapea a HTTP 403 (Forbidden).
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
