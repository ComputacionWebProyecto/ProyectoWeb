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
@SQLDelete(sql = "UPDATE edge SET status = 'inactive' WHERE id = ?")

@Table(name = "edge")
public class Edge{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 16)
    private String status;


    @ManyToOne(fetch = FetchType.LAZY)// N aristas → 1 proceso (carga diferida)
    @JoinColumn(name = "process_id", nullable = false) // FK a Process obligatoria
    private Process process;

    @ManyToOne
    @JoinColumn(name = "activity_source_id", nullable = false)
    private Activity activitySource;

    @ManyToOne
    @JoinColumn(name = "activity_destiny_id", nullable = false)
    private Activity activityDestiny;

}





