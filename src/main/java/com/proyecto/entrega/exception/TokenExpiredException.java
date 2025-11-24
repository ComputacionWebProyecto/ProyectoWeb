package com.proyecto.entrega.exception;

/**
 * Excepción para indicar que el token JWT ha expirado.
 * 
 * Mapea a HTTP 401 (Unauthorized) pero permite al frontend
 * distinguir entre un token expirado (puede renovar) y un token inválido
 * (debe re-autenticar).
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
