package com.proyecto.entrega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.entrega.entity.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long>{

    /**
     * Busca activities por processId.
     *
     * El filtro @Where de la entidad asegura que solo se retornen activities activas.
     *
     * @param processId Identificador del proceso
     * @return Lista de activities activas del proceso
     */
    List<Activity> findByProcessId(Long processId);

    /**
     * Busca activities por processId y status específico.
     *
     * Este método utiliza una query nativa para evitar el filtro @Where
     * de la entidad Activity, permitiendo buscar activities inactivas.
     *
     * @param processId Identificador del proceso
     * @param status Estado de la activity ('active' o 'inactive')
     * @return Lista de activities que coinciden con los criterios
     */
    @Query(value = "SELECT * FROM activity WHERE process_id = :processId AND status = :status",
           nativeQuery = true)
    List<Activity> findByProcessIdAndStatus(@Param("processId") Long processId,
                                           @Param("status") String status);

}
