package com.proyecto.entrega.entity;


import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@SQLDelete(sql = "UPDATE gateway SET status = 'inactive' WHERE id = ?")

@Table(name = "gateway")
public class Gateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private String status; //aceptado o no aceptado

    @Enumerated(EnumType.STRING)// Guarda enum como texto
    @Column(nullable = false)
    private String type; //exclusivos,	paralelos	o	inclusivos

    @ManyToOne(fetch = FetchType.LAZY)// Muchos gateways → 1 proceso (carga diferida)
    @JoinColumn(name = "process_id", nullable = false)
    private Process process;
}
