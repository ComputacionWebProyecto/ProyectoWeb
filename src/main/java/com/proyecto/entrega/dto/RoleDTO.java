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
public class RoleDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long companyId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CompanyDTO company;
    @JsonIgnore
    private List<UserSafeDTO> users;
    @JsonIgnore
    private List<ActivityDTO> activities;
}