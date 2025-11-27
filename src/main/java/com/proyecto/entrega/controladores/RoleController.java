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

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping()
    @PreAuthorize("hasRole('administrador')")
    public RoleDTO createRole(Authentication authentication, @RequestBody RoleDTO role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return roleService.createRole(role);
    }

    @PutMapping()
    @PreAuthorize("hasRole('administrador')")
    public RoleDTO updateRole(Authentication authentication, @RequestBody RoleDTO role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return roleService.updateRole(role);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('administrador')")
    public void deleteRole(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        roleService.deleteRole(id);
    }

    @GetMapping(value = "/{id}")
    public RoleDTO getRole(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
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
        return roleService.getUsersByCompany(id);
    }

}
