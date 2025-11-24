package com.proyecto.entrega.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.dto.AuthorizedDTO;
import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

    private final CompanyService companyService;
    private final UserService userService;
    private final RoleService roleService;
    private final ProcessService processService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public RegistrationService(CompanyService companyService, UserService userService,
            RoleService roleService, ProcessService processService, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.companyService = companyService;
        this.userService = userService;
        this.roleService = roleService;
        this.processService = processService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    /**
     * Registra una nueva empresa con su usuario administrador.
     *
     * Este método ejecuta de forma transaccional la creación de una empresa,
     * su primer proceso, el rol de administrador y el usuario administrador inicial.
     *
     * @param companyDTO Datos de la empresa a crear
     * @param userDTO Datos del usuario administrador
     * @return UserDTO del usuario administrador creado
     */
    @Transactional
    public AuthorizedDTO registerCompanyAndAdmin(CompanyDTO companyDTO, UserDTO userDTO) throws JsonProcessingException {
        // 1. Crear la compañía
        CompanyDTO createdCompany = companyService.createCompany(companyDTO);

        // 2. Crear proceso inicial de la empresa
        ProcessDTO process = new ProcessDTO();
        process.setName("Mi primer proceso");
        process.setDescription("Aquí colocarás la descripción de cada proceso");
        process.setCompanyId(createdCompany.getId());
        processService.createProcess(process);

        // 3. Crear rol administrador para la empresa
        RoleDTO role = new RoleDTO();
        role.setNombre("administrador");
        role.setDescripcion("usuario administrador total de la compañia");
        role.setCompanyId(createdCompany.getId());
        RoleDTO createdRole = roleService.createRole(role);

        // 4. Crear usuario administrador
        userDTO.setCompanyId(createdCompany.getId());
        userDTO.setRoleId(createdRole.getId());
        UserDTO createdUser = userService.createUser(userDTO);

        // 5. Crear usuario safe DTO
        UserSafeDTO userSafe = new UserSafeDTO();
        userSafe.setId(createdUser.getId());
        userSafe.setCorreo(createdUser.getCorreo());
        userSafe.setNombre(createdUser.getNombre());
        userSafe.setStatus(createdUser.getStatus());
        userSafe.setCompanyId(createdCompany.getId());
        userSafe.setRoleId(createdRole.getId());
        userSafe.setCompany(createdCompany);
        userSafe.setRole(createdRole);

        // 6. Generar JWT token
        String userJson = objectMapper.writeValueAsString(userSafe);
        String roleName = createdRole.getNombre();
        String token = jwtUtil.generateToken(userJson, roleName);
        System.out.println("usuario compañia: "+ userSafe.getCompanyId()+ ", usuario role: "+ userSafe.getRoleId());

        // 7. Retornar AuthorizedDTO con usuario, token y tipo
        return new AuthorizedDTO(userSafe, token, "Bearer");
    }
}
