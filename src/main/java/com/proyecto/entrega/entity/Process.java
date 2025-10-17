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
import jakarta.persistence.Table;
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
@SQLDelete(sql = "UPDATE process SET status = 'inactive' WHERE id = ?")

@Table(name = "process")
public class Process{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; 
    private String description;
    private String status = "active";

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "process")
    private List<Gateway> gateways;

    @OneToMany(mappedBy = "process")
    private List<Role> roles;

    @OneToMany(mappedBy = "process")
    private List<Edge> edges;

    @OneToMany(mappedBy = "process")
    private List<Activity> activities;

    
}





