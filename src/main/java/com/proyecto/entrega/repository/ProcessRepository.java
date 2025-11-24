package com.proyecto.entrega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.entrega.entity.Process;

public interface ProcessRepository extends JpaRepository<Process, Long> {

    List<Process> findByCompanyId(Long id);
    
    /**
     * Busca procesos por companyId y status específico.
     *
     * Este método utiliza una query nativa para evitar el filtro @Where
     * de la entidad Process, permitiendo buscar procesos inactivos.
     *
     * @param companyId Identificador de la empresa
     * @param status Estado del proceso ('active' o 'inactive')
     * @return Lista de procesos que coinciden con los criterios
     */
    @Query(value = "SELECT * FROM process WHERE company_id = :companyId AND status = :status",
           nativeQuery = true)
    List<Process> findByCompanyIdAndStatus(@Param("companyId") Long companyId,
                                          @Param("status") String status);

       /**
        * Verifica si existe un proceso con el nombre especificado en una compañía.
        *
        * @param name      Nombre del proceso
        * @param companyId Identificador de la empresa
        * @return true si existe, false en caso contrario
        */
       boolean existsByNameAndCompanyId(String name, Long companyId);

}