package com.proyecto.entrega.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("deprecation")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'active'")
@SQLDelete(sql = "UPDATE company SET status = 'inactive' WHERE id = ?")

@Table(name = "company")
public class Company{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long NIT;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "correo_contacto", nullable = false)
    private String correoContacto;

    @Column(nullable = false, length = 16)
    private String status;

    // 1 Empresa -> N Procesos
    // mappedBy="company" indica que la FK viene de Process
    // cascade=ALL: operar Empresa propagará a sus Process (persist, remove, etc.)
    // orphanRemoval=true: si se saca (borra) un Process de la lista, JPA lo elimina (huérfano)
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Process> processes;

    // 1 Empresa -> N Usuarios
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> user;

}





