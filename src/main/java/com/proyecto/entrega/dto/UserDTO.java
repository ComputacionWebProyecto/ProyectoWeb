package com.proyecto.entrega.dto;



import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long companyId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long roleId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CompanyDTO company;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private RoleDTO role;
}
