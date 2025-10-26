package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.ProcessRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProcessService {

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ModelMapper modelMapper;

    public ProcessDTO createProcess(ProcessDTO processDTO) {

        if (processDTO.getCompanyId() == null){
            throw new IllegalArgumentException("CompanyId is required");
        }
        
        Process process = modelMapper.map(processDTO, Process.class);

        Company company = companyService.findCompanyEntity(processDTO.getCompanyId());

        process.setCompany(company);

        process = processRepository.save(process);
        
        return modelMapper.map(process, ProcessDTO.class);
    }

    public ProcessDTO updateProcess(ProcessDTO processDTO) {

        if(processDTO.getId() == null){
            throw new IllegalArgumentException("Id is required");
        }

        if (processDTO.getCompanyId() == null){
            throw new IllegalArgumentException("CompanyId is required");
        }

        Process process = processRepository.findById(processDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Process " + processDTO.getId() + " not found"));
        
        Company company = companyService.findCompanyEntity(processDTO.getCompanyId()); 

        // Actualizar valores 
        process.setName(processDTO.getName());
        process.setDescription(processDTO.getDescription());
        process.setCompany(company);

        process = processRepository.save(process);

        return modelMapper.map(process, ProcessDTO.class);
    }

    public ProcessDTO findProcess(Long id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Process " + id + " not found"));

        return modelMapper.map(process, ProcessDTO.class);
    }

    public Process findProcessEntity(Long id) {
        return processRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Process " + id + " not found"));
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
                .map(process -> modelMapper.map(process, ProcessDTO.class))
                .toList();
    }

    public List<ProcessDTO> getProcessesByCompany(Long id) {
        List<Process> processes = processRepository.findByCompanyId(id);
        return processes.stream()
                .map(process -> modelMapper.map(process, ProcessDTO.class))
                .toList();
    }
}
