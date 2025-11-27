package com.proyecto.entrega.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.service.RoleService;
import com.proyecto.entrega.exception.UnauthorizedAccessException;
import com.proyecto.entrega.security.SecurityHelper;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private SecurityHelper securityHelper;

    @PostMapping()
    @PreAuthorize("hasRole('administrador')")
    public RoleDTO createRole(Authentication authentication, @RequestBody RoleDTO role) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        securityHelper.validateCompanyAccess(authentication, role.getCompanyId());
        return roleService.createRole(role);
    }

    @PutMapping()
    @PreAuthorize("hasRole('administrador')")
    public RoleDTO updateRole(Authentication authentication, @RequestBody RoleDTO role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        RoleDTO existing = roleService.findRole(role.getId());
        securityHelper.validateCompanyResourceAccess(
                authentication,
                existing.getCompany().getId());

        securityHelper.validateCompanyAccess(authentication, role.getCompanyId());
        return roleService.updateRole(role);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('administrador')")
    public void deleteRole(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        RoleDTO role = roleService.findRole(id);
        securityHelper.validateCompanyAccess(
                authentication,
                role.getCompany().getId());

        roleService.deleteRole(id);
    }

    @GetMapping(value = "/{id}")
    public RoleDTO getRole(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        RoleDTO role = roleService.findRole(id);

        securityHelper.validateCompanyResourceAccess(
                authentication,
                role.getCompany().getId());
        return roleService.findRole(id);
    }

    @GetMapping()
    public List<RoleDTO> getRoles(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return roleService.findRoles();
    }

    @GetMapping(value = "/company/{id}")
    public List<RoleDTO> getRolesByCompany(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        securityHelper.validateCompanyAccess(authentication, id);
        return roleService.getUsersByCompany(id);
    }

}
