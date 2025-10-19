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

    @Transactional
    public UserDTO registerCompanyAndAdmin(CompanyDTO companyDTO, UserDTO userDTO) {
        // 1. Crear la compañía
        CompanyDTO createdCompany = companyService.createCompany(companyDTO);

        // 2. Crear proceso inicial
        ProcessDTO process = new ProcessDTO();
        process.setName("Proceso inicial");
        process.setDescription("Primer proceso");
        process.setCompanyId(createdCompany.getId());
        processService.createProcess(process);

        // 3. Crear rol admin
        RoleDTO role = new RoleDTO();
        role.setNombre("administrador");
        role.setDescripcion("usuario administrador total de la compañia");
        role.setCompanyId(createdCompany.getId());
        RoleDTO createdRole = roleService.createRole(role);

        // 4. Crear usuario admin
        userDTO.setCompanyId(createdCompany.getId());
        userDTO.setRoleId(createdRole.getId());
        return userService.createUser(userDTO);
    }
}

