package com.proyecto.entrega.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.entrega.entity.User;


public interface UserRepository extends JpaRepository<User, Long>{

}


