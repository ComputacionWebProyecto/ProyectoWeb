package com.proyecto.entrega.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@SuppressWarnings("deprecation")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'active'")
@SQLDelete(sql = "UPDATE usuarios SET status = 'inactive' WHERE id = ?")

@Table(name = "usuarios")
public class User{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Esto ayuda a proteger la unicidad de "correo" a nivel de BD, el unique
    @Column(nullable = false, unique = true)
    private String correo;

    // nullable = false significa que ese campo no puede ser nulo
    @Column(nullable = false)
    private String contrasena;

    // LAZY reduce carga al traer Usuario sin necesidad de traer Empresa/Rol
    // por defecto es EAGER en @ManyToOne en JPA, puede traer más datos de los necesarios.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Muchos usuarios tienen un rol; LAZY = carga diferida
    //Lazy solo carga el Rol cuando se necesita, no de inmediato
    //solo carga el Rol cuando se accede a él, no inmediatamente
    //Evita traer datos innecesarios al consultar usuarios.
    @ManyToOne(fetch = FetchType.LAZY) // Muchos usuarios tienen un rol; LAZY = carga diferida
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    //deberia haber status aqui?

}





