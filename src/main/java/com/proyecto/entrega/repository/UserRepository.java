package com.proyecto.entrega.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.entrega.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

    boolean existsByCorreo(String correo);
    Optional<User> findByCorreo(String correo);

}



