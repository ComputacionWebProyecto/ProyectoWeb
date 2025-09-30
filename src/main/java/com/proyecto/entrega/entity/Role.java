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

@SuppressWarnings("deprecation")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'active'")
@SQLDelete(sql = "UPDATE role SET status = 'inactive' WHERE id = ?")

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private String status = "active";

    // Relación N:1 con Company
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    // Relación 1:N con usuarios (varios usuarios pueden tener el mismo rol)
    @OneToMany(mappedBy = "role")
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;
}
