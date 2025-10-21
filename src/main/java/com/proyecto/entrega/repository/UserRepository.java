package com.proyecto.entrega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.entrega.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

    boolean existsByCorreo(String correo);
    List<User> findByCompanyIdAndIdNot(Long id, Long excludedUserId);
    User findByCorreo(String correo);

}



