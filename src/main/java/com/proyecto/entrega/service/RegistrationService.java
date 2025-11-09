package com.proyecto.entrega.service;

import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.dto.UserDTO;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

    private final CompanyService companyService;
    private final UserService userService;
    private final RoleService roleService;
    private final ProcessService processService;

    public RegistrationService(CompanyService companyService, UserService userService,
                               RoleService roleService, ProcessService processService) {
        this.companyService = companyService;
        this.userService = userService;
        this.roleService = roleService;
        this.processService = processService;
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
    public UserDTO registerCompanyAndAdmin(CompanyDTO companyDTO, UserDTO userDTO) {
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
        return userService.createUser(userDTO);
    }
}

