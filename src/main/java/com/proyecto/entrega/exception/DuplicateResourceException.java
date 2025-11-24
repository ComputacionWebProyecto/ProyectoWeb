package com.proyecto.entrega.exception;

/**
 * Excepción para indicar que se intenta crear un recurso que ya existe.
 * 
 * Esta excepción se mapea a HTTP 409 (Conflict).
 */
public class DuplicateResourceException extends RuntimeException {
    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Constructor con mensaje simple
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructor con detalles del recurso duplicado
     * 
     * @param resourceName Nombre del recurso (ej: "Usuario", "Compañía")
     * @param fieldName    Nombre del campo duplicado (ej: "email", "nombre")
     * @param fieldValue   Valor duplicado
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s con %s '%s' ya existe", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
