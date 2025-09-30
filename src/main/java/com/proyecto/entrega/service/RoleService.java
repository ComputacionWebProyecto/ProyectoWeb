package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.repository.CompanyRepository;
import com.proyecto.entrega.repository.ProcessRepository;
import com.proyecto.entrega.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RoleService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ProcessRepository processRepository;

    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = modelMapper.map(roleDTO, Role.class);

        // Asignar relaciones manualmente
        if (roleDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(roleDTO.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company not found"));
            role.setCompany(company);
        }

        if (roleDTO.getProcessId() != null) {
            Process process = processRepository.findById(roleDTO.getProcessId())
                    .orElseThrow(() -> new EntityNotFoundException("Process not found"));
            role.setProcess(process);
        }

        role = roleRepository.save(role);
        return modelMapper.map(role, RoleDTO.class);
    }

    public RoleDTO updateRole(RoleDTO roleDTO) {
        Role role = roleRepository.findById(roleDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Role " + roleDTO.getId() + " not found"));

        // Actualizar campos básicos
        role.setNombre(roleDTO.getNombre());
        role.setDescripcion(roleDTO.getDescripcion());

        // Actualizar relaciones
        if (roleDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(roleDTO.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company not found"));
            role.setCompany(company);
        }

        if (roleDTO.getProcessId() != null) {
            Process process = processRepository.findById(roleDTO.getProcessId())
                    .orElseThrow(() -> new EntityNotFoundException("Process not found"));
            role.setProcess(process);
        }

        role = roleRepository.save(role);
        return modelMapper.map(role, RoleDTO.class);
    }

    public RoleDTO findRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role " + id + " not found"));
        return modelMapper.map(role, RoleDTO.class);
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
}
