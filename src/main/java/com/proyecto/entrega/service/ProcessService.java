package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.ProcessRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProcessService {
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ProcessDTO createProcess(ProcessDTO processDTO) {
        Process process = modelMapper.map(processDTO, Process.class);
        process = processRepository.save(process);
        return modelMapper.map(process, ProcessDTO.class);
    }

    public ProcessDTO updateProcess(ProcessDTO processDTO) {
        Process process = modelMapper.map(processDTO, Process.class);
        process = processRepository.save(process);
        return modelMapper.map(process, ProcessDTO.class);
    }

     public ProcessDTO findProcess(Long id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Process " + id + " not found"));
        return modelMapper.map(process, ProcessDTO.class);
    }

    public void deleteProcess(Long id) {
        processRepository.deleteById(id);
    }

    public List<ProcessDTO> findProcesses() {
        List<Process> processes = processRepository.findAll();
        return processes.stream()
                .map(process -> modelMapper.map(process, ProcessDTO.class))
                .toList();
    }

}
