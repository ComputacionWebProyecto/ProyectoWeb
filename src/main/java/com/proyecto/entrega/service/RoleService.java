package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RoleService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompanyService companyService;

    public RoleDTO createRole(RoleDTO roleDTO) {

        if (roleDTO.getCompanyId() == null) {
            throw new IllegalArgumentException("CompanyId is required for create");
        }
        Role role = modelMapper.map(roleDTO, Role.class);

        Company company = companyService.findCompanyEntity(roleDTO.getCompanyId());

        role.setCompany(company);

        role = roleRepository.save(role);
        return modelMapper.map(role, RoleDTO.class);
    }

    public RoleDTO updateRole(RoleDTO roleDTO) {

        if (roleDTO.getId() == null) {
            throw new IllegalArgumentException("Id is required for create");
        }

        if (roleDTO.getCompanyId() == null) {
            throw new IllegalArgumentException("CompanyId is required for create");
        }

        Role role = roleRepository.findById(roleDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Role " + roleDTO.getId() + " not found"));

        Company company = companyService.findCompanyEntity(roleDTO.getCompanyId());

        // Actualizar campos
        role.setNombre(roleDTO.getNombre());
        role.setDescripcion(roleDTO.getDescripcion());
        role.setCompany(company);

        role = roleRepository.save(role);
        return modelMapper.map(role, RoleDTO.class);
    }

    public RoleDTO findRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role " + id + " not found"));
        return modelMapper.map(role, RoleDTO.class);
    }

    public Role findRoleEntity(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role " + id + " not found"));
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role " + id + " not found"));
        role.setStatus("inactive");
        roleRepository.save(role);
    }

    public List<RoleDTO> findRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .toList();
    }

    public List<RoleDTO> getUsersByCompany(Long id) {
        List<Role> roles = roleRepository.findByCompanyId(id);
        return roles.stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .toList();

    }
}
