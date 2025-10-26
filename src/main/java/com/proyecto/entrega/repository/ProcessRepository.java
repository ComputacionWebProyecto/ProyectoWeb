package com.proyecto.entrega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.entrega.entity.Process;

public interface ProcessRepository extends JpaRepository<Process, Long> {

    List<Process> findByCompanyId(Long id);
    
}