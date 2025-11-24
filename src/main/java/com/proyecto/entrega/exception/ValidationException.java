package com.proyecto.entrega.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Excepción para errores de validación de datos de entrada.
 * 
 * Esta excepción se mapea a HTTP 400 (Bad Request)
 */
public class ValidationException extends RuntimeException {
    private Map<String, String> errors;

    /**
     * Constructor con un mensaje simple
     */
    public ValidationException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }

    /**
     * Constructor con un mapa de errores por campo
     * 
     * @param errors Mapa donde la clave es el nombre del campo y el valor es el
     *               mensaje de error
     */
    public ValidationException(Map<String, String> errors) {
        super("Errores de validación");
        this.errors = errors != null ? errors : new HashMap<>();
    }

    /**
     * Constructor con mensaje y mapa de errores
     */
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors != null ? errors : new HashMap<>();
    }

    /**
     * Obtiene el mapa de errores de validación
     */
    public Map<String, String> getErrors() {
        return errors;
    }

    /**
     * Verifica si hay errores de validación
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}
