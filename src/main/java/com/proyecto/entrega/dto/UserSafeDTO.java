package com.proyecto.entrega.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSafeDTO {

    private Long id;
    private String nombre;
    private String correo;
    private Long companyId;
    private Long roleId;
}
