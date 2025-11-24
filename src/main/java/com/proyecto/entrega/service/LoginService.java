package com.proyecto.entrega.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.dto.AuthorizedDTO;
import com.proyecto.entrega.dto.LoginDTO;
import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.exception.InvalidCredentialsException;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.security.JwtUtil;

@Service
public class LoginService {

    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    public LoginService(UserService userService, CompanyService companyService,
            RoleService roleService) {
        this.userService = userService;
    }

    public AuthorizedDTO authenticate(LoginDTO loginDTO) throws JsonProcessingException {
        // 0. Validar formato de datos
        if (loginDTO.getCorreo() == null || loginDTO.getCorreo().trim().isEmpty()) {
            throw new ValidationException("El correo electrónico es requerido");
        }
        if (loginDTO.getContrasena() == null || loginDTO.getContrasena().isEmpty()) {
            throw new ValidationException("La contraseña es requerida");
        }

        // 1. Buscar usuario por correo
        UserDTO user;
        try {
            user = userService.findByEmail(loginDTO.getCorreo());
        } catch (ResourceNotFoundException e) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        // 2. Verificar contraseña con BCrypt
        boolean contrasenaCorrecta = passwordEncoder.matches(
                loginDTO.getContrasena(),
                user.getContrasena());

        if (!contrasenaCorrecta) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        // 3. Crear UserSafeDTO (sin contraseña)
        UserSafeDTO userSafe = new UserSafeDTO();
        userSafe.setId(user.getId());
        userSafe.setCorreo(user.getCorreo());
        userSafe.setNombre(user.getNombre());
        userSafe.setCompanyId(user.getCompany().getId());
        userSafe.setRoleId(user.getRole().getId());
        userSafe.setCompany(user.getCompany());
        userSafe.setRole(user.getRole());

        // 4. Generar JWT token
        String userJson = objectMapper.writeValueAsString(userSafe);
        String role = user.getRole().getNombre();
        String token = jwtUtil.generateToken(userJson, role);

        // 5. Retornar AuthorizedDTO con usuario, token y tipo
        return new AuthorizedDTO(userSafe, token, "Bearer");
    }
}