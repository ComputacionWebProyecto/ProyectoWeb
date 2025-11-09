package com.proyecto.entrega.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.entrega.entity.Edge;

public interface EdgeRepository extends JpaRepository<Edge, Long> {

    /**
     * Busca edges por processId.
     *
     * El filtro @Where de la entidad asegura que solo se retornen edges activos.
     */
    List<Edge> findByProcessId(Long processId);

    /**
     * Busca edges por processId y status específico.
     *
     * Este método utiliza una query nativa para evitar el filtro @Where
     * de la entidad Edge, permitiendo buscar edges inactivos.
     */
    @Query(value = "SELECT * FROM edge WHERE process_id = :processId AND status = :status",
           nativeQuery = true)
    List<Edge> findByProcessIdAndStatus(@Param("processId") Long processId,
                                       @Param("status") String status);

    /**
     * Busca edges conectados a una activity específica (como origen o destino).
     *
     * Retorna edges donde:
     * - La activity es el origen (activitySource)
     * - La activity es el destino (activityDestiny)
     * - O los campos tipados indican fromType='activity' y fromId=activityId
     * - O los campos tipados indican toType='activity' y toId=activityId
     */
    @Query("SELECT e FROM Edge e WHERE e.activitySource.id = :activityId OR e.activityDestiny.id = :activityId " +
           "OR (e.fromType = 'activity' AND e.fromId = :activityId) " +
           "OR (e.toType = 'activity' AND e.toId = :activityId)")
    List<Edge> findByActivityId(@Param("activityId") Long activityId);

    /**
     * Busca edges conectados a un gateway específico (como origen o destino).
     *
     * Retorna edges donde:
     * - fromType='gateway' y fromId=gatewayId (gateway es origen)
     * - toType='gateway' y toId=gatewayId (gateway es destino)
     */
    @Query("SELECT e FROM Edge e WHERE " +
           "(e.fromType = 'gateway' AND e.fromId = :gatewayId) " +
           "OR (e.toType = 'gateway' AND e.toId = :gatewayId)")
    List<Edge> findByGatewayId(@Param("gatewayId") Long gatewayId);

    /**
     * Busca un edge por ID ignorando el filtro de status.
     *
     * Útil para reactivar edges marcados como 'inactive'.
     */
    @Query(value = "SELECT * FROM edge WHERE id = :id", nativeQuery = true)
    Optional<Edge> findByIdIgnoreStatus(@Param("id") Long id);
}
