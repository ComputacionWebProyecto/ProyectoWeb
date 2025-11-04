package com.proyecto.entrega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.entrega.entity.Gateway;

public interface GatewayRepository extends JpaRepository<Gateway, Long>{

    /**
     * Busca gateways por processId.
     *
     * El filtro @Where de la entidad asegura que solo se retornen gateways activos.
     *
     * @param processId Identificador del proceso
     * @return Lista de gateways activos del proceso
     */
    List<Gateway> findByProcessId(Long processId);

    /**
     * Busca gateways por processId y status específico.
     *
     * Este método utiliza una query nativa para evitar el filtro @Where
     * de la entidad Gateway, permitiendo buscar gateways inactivos.
     *
     * @param processId Identificador del proceso
     * @param status Estado del gateway ('active' o 'inactive')
     * @return Lista de gateways que coinciden con los criterios
     */
    @Query(value = "SELECT * FROM gateway WHERE process_id = :processId AND status = :status",
           nativeQuery = true)
    List<Gateway> findByProcessIdAndStatus(@Param("processId") Long processId,
                                          @Param("status") String status);

}
