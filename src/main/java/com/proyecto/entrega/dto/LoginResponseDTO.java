package com.proyecto.entrega.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String message;
    private UserDTO user;
    private CompanyDTO company;
    private RoleDTO role;
}