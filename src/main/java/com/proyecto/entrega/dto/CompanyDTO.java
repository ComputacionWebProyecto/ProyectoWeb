package com.proyecto.entrega.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CompanyDTO {
    private Long id;
    @JsonProperty("NIT")
    private Long nit;
    private String name;
    private String correoContacto;
    private String status;

    @JsonIgnore
    private List<ProcessDTO> processes;
    @JsonIgnore
    private List<UserSafeDTO> users;
}
