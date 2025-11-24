package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.exception.DuplicateResourceException;
import com.proyecto.entrega.repository.CompanyRepository;

import jakarta.persistence.EntityNotFoundException;

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
            throw new IllegalArgumentException("Id is required for update");
        }

        Company company = companyRepository.findById(companyDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Company " + companyDTO.getId() + " not found"));

        company.setName(companyDTO.getName());
        company.setNit(companyDTO.getNit());
        company.setCorreoContacto(companyDTO.getCorreoContacto());

        company = companyRepository.save(company);
        return modelMapper.map(company, CompanyDTO.class);
    }

    public CompanyDTO findCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company " + id + " not found"));
        return modelMapper.map(company, CompanyDTO.class);
    }

    public Company findCompanyEntity(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company " + id + " not found"));
    }

    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company " + id + " not found"));
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