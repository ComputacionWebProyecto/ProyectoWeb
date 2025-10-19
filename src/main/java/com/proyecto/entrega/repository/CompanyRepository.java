package com.proyecto.entrega.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.entrega.entity.Company;


public interface CompanyRepository extends JpaRepository<Company, Long>{

    boolean existsByName(String name);

    boolean existsByCorreoContacto(String correoContacto);

}
