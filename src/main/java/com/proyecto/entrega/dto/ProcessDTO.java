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


public class ProcessDTO {
    private Long id;
    private String name; 
    private String status;
    private String description;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long companyId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CompanyDTO company;

    @JsonIgnore
    private List<GatewayDTO> gateways;
    @JsonIgnore
    private List<RoleDTO> roles;
    @JsonIgnore
    private List<EdgeDTO> edges;
    @JsonIgnore
    private List<ActivityDTO> activities;
}
