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

@RestController
@RequestMapping("/api/role")
public class RoleController {
     @Autowired
    private RoleService roleService;

    @PostMapping()
    public void createRole(@RequestBody RoleDTO role) {
        roleService.createRole(role);
    }

    @PutMapping()
    public void updateRole(@RequestBody RoleDTO role) {
        roleService.updateRole(role);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

    @GetMapping(value = "/{id}")
    public RoleDTO getRole(@PathVariable Long id) {
        return roleService.findRole(id);
    }

    @GetMapping()
    public List<RoleDTO> getRoles() {
        return roleService.findRoles();
    }
    
}
