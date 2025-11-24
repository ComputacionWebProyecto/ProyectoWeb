package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.RoleRepository;

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
            throw new ValidationException("El ID de la compañía es requerido para crear");
        }
        Role role = modelMapper.map(roleDTO, Role.class);

        Company company = companyService.findCompanyEntity(roleDTO.getCompanyId());

        role.setCompany(company);

        role = roleRepository.save(role);
        return modelMapper.map(role, RoleDTO.class);
    }

    public RoleDTO updateRole(RoleDTO roleDTO) {

        if (roleDTO.getId() == null) {
            throw new ValidationException("El ID es requerido para actualizar");
        }

        if (roleDTO.getCompanyId() == null) {
            throw new ValidationException("El ID de la compañía es requerido para actualizar");
        }

        Role role = roleRepository.findById(roleDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", roleDTO.getId()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));
        return modelMapper.map(role, RoleDTO.class);
    }

    public Role findRoleEntity(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));
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
