package com.proyecto.entrega.service;

import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.dto.LoginDTO;
import com.proyecto.entrega.dto.LoginResponseDTO;
import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.exception.InvalidCredentialsException;

@Service
public class LoginService {

    private final UserService userService;
    private final CompanyService companyService;
    private final RoleService roleService;

    public LoginService(UserService userService, CompanyService companyService,
                       RoleService roleService) {
        this.userService = userService;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public UserDTO authenticate(LoginDTO loginDTO) {
        // 1. Buscar usuario por correo
        UserDTO user = userService.findByEmail(loginDTO.getCorreo());

        // 2. Verificar contraseña
        if (!loginDTO.getContrasena().equals(user.getContrasena())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
        
        return user;
    }
}