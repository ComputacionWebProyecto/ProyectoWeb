package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.repository.RoleRepository;

public class RoleService {
    
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RoleRepository roleRepository;

    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = modelMapper.map(roleDTO, Role.class);
        role = roleRepository.save(role);
        return modelMapper.map(role, RoleDTO.class);
    }

    public RoleDTO updateRole(RoleDTO roleDTO) {
        Role role = modelMapper.map(roleDTO, Role.class);
        role = roleRepository.save(role);
        return modelMapper.map(role, RoleDTO.class);
    }
        

    public RoleDTO findRole(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        return modelMapper.map(role, RoleDTO.class);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public List<RoleDTO> findRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .toList();
    }

}



