package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.exception.DuplicateResourceException;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CompanyRepository companyRepository;

    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        if (companyRepository.existsByName(companyDTO.getName())) {
            throw new DuplicateResourceException("Compañía", "nombre", companyDTO.getName());
        }
        if (companyRepository.existsByCorreoContacto(companyDTO.getCorreoContacto())) {
            throw new DuplicateResourceException("Compañía", "correo", companyDTO.getCorreoContacto());
        }
        Company company = modelMapper.map(companyDTO, Company.class);
        company = companyRepository.save(company);
        return modelMapper.map(company, CompanyDTO.class);
    }

    public CompanyDTO updateCompany(CompanyDTO companyDTO) {
        if (companyDTO.getId() == null) {
            throw new ValidationException("El ID es requerido para actualizar");
        }

        Company company = companyRepository.findById(companyDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Compañía", "id", companyDTO.getId()));

        company.setName(companyDTO.getName());
        company.setNit(companyDTO.getNit());
        company.setCorreoContacto(companyDTO.getCorreoContacto());

        company = companyRepository.save(company);
        return modelMapper.map(company, CompanyDTO.class);
    }

    public CompanyDTO findCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compañía", "id", id));
        return modelMapper.map(company, CompanyDTO.class);
    }

    public Company findCompanyEntity(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compañía", "id", id));
    }

    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compañía", "id", id));
        company.setStatus("inactive");
        companyRepository.save(company);

    }

    public List<CompanyDTO> findCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(company -> modelMapper.map(company, CompanyDTO.class))
                .toList();
    }

}