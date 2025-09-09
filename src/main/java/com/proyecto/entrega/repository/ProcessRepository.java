package com.proyecto.entrega.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.entrega.entity.Process;

public interface ProcessRepository extends JpaRepository<Process, Long> {

}