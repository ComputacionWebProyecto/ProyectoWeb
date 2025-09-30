package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.CompanyRepository;
import com.proyecto.entrega.repository.ProcessRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProcessService {

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ProcessDTO createProcess(ProcessDTO processDTO) {
        // Mapear lo básico
        Process process = modelMapper.map(processDTO, Process.class);

        // Asignar la compañía si viene el companyId
        if (processDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(processDTO.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company not found"));
            process.setCompany(company);
        }

        process = processRepository.save(process);

        // Devolver DTO con companyId relleno
        ProcessDTO dto = modelMapper.map(process, ProcessDTO.class);
        dto.setCompanyId(process.getCompany().getId());
        return dto;
    }

    public ProcessDTO updateProcess(ProcessDTO processDTO) {
        Process process = processRepository.findById(processDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Process " + processDTO.getId() + " not found"));

        // Actualizar valores básicos
        process.setName(processDTO.getName());
        process.setDescription(processDTO.getDescription());

        // Si viene un companyId, actualizar la relación
        if (processDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(processDTO.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company not found"));
            process.setCompany(company);
        }

        process = processRepository.save(process);

        ProcessDTO dto = modelMapper.map(process, ProcessDTO.class);
        dto.setCompanyId(process.getCompany().getId());
        return dto;
    }

    public ProcessDTO findProcess(Long id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Process " + id + " not found"));

        ProcessDTO dto = modelMapper.map(process, ProcessDTO.class);
        dto.setCompanyId(process.getCompany().getId());
        return dto;
    }

    public void deleteProcess(Long id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Process " + id + " not found"));
        process.setStatus("inactive");
        processRepository.save(process);
    }

    public List<ProcessDTO> findProcesses() {
        List<Process> processes = processRepository.findAll();
        return processes.stream()
                .map(process -> {
                    ProcessDTO dto = modelMapper.map(process, ProcessDTO.class);
                    dto.setCompanyId(process.getCompany().getId());
                    return dto;
                })
                .toList();
    }
}
