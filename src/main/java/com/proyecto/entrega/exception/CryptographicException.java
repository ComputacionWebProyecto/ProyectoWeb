package com.proyecto.entrega.exception;

/**
 * Excepción para errores relacionados con operaciones criptográficas.
 * Se mapea a HTTP 500 (Internal Server Error).
 */
public class CryptographicException extends RuntimeException {

    /**
     * Constructor con mensaje
     */
    public CryptographicException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     */
    public CryptographicException(String message, Throwable cause) {
        super(message, cause);
    }
}
