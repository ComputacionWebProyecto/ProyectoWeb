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

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.service.ProcessService;
@RestController
@RequestMapping("/api/process")
public class ProcessController {
    @Autowired
    private ProcessService processService;

    @PostMapping()
    public ProcessDTO createProcess(@RequestBody ProcessDTO process) {
        return processService.createProcess(process);
    }

    @PutMapping()
    public ProcessDTO updateProcess(@RequestBody ProcessDTO  process) {
        return processService.updateProcess(process);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteProcess(@PathVariable Long id) {
        processService.deleteProcess(id);
    }

    @GetMapping(value = "/{id}")
    public ProcessDTO getProcess(@PathVariable Long id) {
        return processService.findProcess(id);
    }

    @GetMapping()
    public List<ProcessDTO> getProcesses() {
        return processService.findProcesses();
    }

    @GetMapping(value = "/company/{id}")
    public List<ProcessDTO> getProcessesByCompany(@PathVariable Long id){
        return processService.getProcessesByCompany(id);
    }
}
