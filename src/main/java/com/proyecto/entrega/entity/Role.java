package com.proyecto.entrega.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;


@SuppressWarnings("deprecation")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'active'")
@SQLDelete(sql = "UPDATE role SET status = 'inactive' WHERE id = ?")

@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)// No se puede dejar vacío en la BD
    private String nombre;// Nombre del rol (ej: "Administrador")

    @Column
    private String descripcion;

    // Soft delete
    @Column(nullable = false, length = 16)
    private String status;

    // Muchos roles pertenecen a una empresa (N:1). LAZY evita traer empresa si no se usa.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)// Columna FK obligatoria en la tabla rol
    private Company company;

    // varios usuarios pueden tener el mismo rol.
    // En Usuario.java existe el campo 'role', por eso el mappedBy apunta a "role".
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;
}
