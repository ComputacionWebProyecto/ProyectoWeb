package com.proyecto.entrega.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * EdgeDTO - DTO para transferencia de datos de conexiones
 *
 * Soporta múltiples formatos para máxima compatibilidad:
 *
 * FORMATO LEGACY (Activity→Activity):
 * - activitySourceId, activityDestinyId
 *
 * FORMATO TIPADO (Todos los tipos):
 * - fromType ('activity' | 'gateway')
 * - fromId (ID del nodo origen)
 * - toType ('activity' | 'gateway')
 * - toId (ID del nodo destino)
 *
 * El backend acepta ambos formatos y los normaliza automáticamente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EdgeDTO {
    private Long id;
    private String label;
    private String description;
    private String status;

    // Process
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long processId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ProcessDTO process;

    // ==================== CAMPOS LEGACY (Activity→Activity) ====================

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long activitySourceId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long activityDestinyId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ActivityDTO activitySource;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ActivityDTO activityDestiny;

    // ==================== CAMPOS TIPADOS (Todos los tipos) ====================

    /**
     * Tipo del nodo origen: 'activity' o 'gateway'
     */
    private String fromType;

    /**
     * ID del nodo origen
     */
    private Long fromId;

    /**
     * Tipo del nodo destino: 'activity' o 'gateway'
     */
    private String toType;

    /**
     * ID del nodo destino
     */
    private Long toId;
}


