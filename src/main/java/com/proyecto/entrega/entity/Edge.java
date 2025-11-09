package com.proyecto.entrega.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad Edge - Conexiones entre Activities y Gateways
 *
 * Soporta múltiples tipos de conexiones:
 * - Activity → Activity (campos legacy: activitySource, activityDestiny)
 * - Activity → Gateway (fromType='activity', toType='gateway')
 * - Gateway → Activity (fromType='gateway', toType='activity')
 * - Gateway → Gateway (fromType='gateway', toType='gateway')
 *
 * CAMPOS LEGACY (mantener compatibilidad):
 * - activitySource: activity origen (solo para A→A)
 * - activityDestiny: activity destino (solo para A→A)
 *
 * CAMPOS TIPADOS (nuevo sistema):
 * - fromType: 'activity' o 'gateway'
 * - fromId: ID del nodo origen
 * - toType: 'activity' o 'gateway'
 * - toId: ID del nodo destino
 *
 * SOFT DELETE:
 * Al eliminar, cambia status a 'inactive' en lugar de borrar físicamente.
 */
@SuppressWarnings("deprecation")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'active'")
@SQLDelete(sql = "UPDATE edge SET status = 'inactive' WHERE id = ?")
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    private String description;

    @Column(nullable = false)
    private String status = "active";
    
    // Relación con Process
    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;

    // ==================== CAMPOS LEGACY (Activity→Activity) ====================

    @ManyToOne
    @JoinColumn(name = "activity_source_id")
    private Activity activitySource;

    @ManyToOne
    @JoinColumn(name = "activity_destiny_id")
    private Activity activityDestiny;

    // ==================== CAMPOS TIPADOS (Todos los tipos) ====================

    /**
     * Tipo del nodo origen: 'activity' o 'gateway'
     */
    @Column(name = "from_type", length = 20)
    private String fromType;

    /**
     * ID del nodo origen (activity_id o gateway_id según fromType)
     */
    @Column(name = "from_id")
    private Long fromId;

    /**
     * Tipo del nodo destino: 'activity' o 'gateway'
     */
    @Column(name = "to_type", length = 20)
    private String toType;

    /**
     * ID del nodo destino (activity_id o gateway_id según toType)
     */
    @Column(name = "to_id")
    private Long toId;
}







