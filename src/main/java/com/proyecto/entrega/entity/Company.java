package com.proyecto.entrega.entity;



import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

public class Company{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long nit;
    private String name;
    private String correoContacto;
    private String status = "active";
    

    @OneToMany(mappedBy = "company")
    private List<Process> processes;

    @OneToMany(mappedBy = "company")
    private List<User> user;

}





