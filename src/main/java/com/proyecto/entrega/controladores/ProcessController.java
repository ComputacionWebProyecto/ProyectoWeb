package com.proyecto.entrega.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.dto.ProcessSummaryDTO;
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

    @GetMapping(value = "/company/{companyId}/summary")
    public ResponseEntity<List<ProcessSummaryDTO>> getProcessesSummaryByCompany(@PathVariable Long companyId) {
        List<ProcessSummaryDTO> summaries = processService.getProcessesSummaryByCompany(companyId);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Reactiva un proceso que fue marcado como inactivo mediante soft delete.
     *
     * Este endpoint permite recuperar procesos eliminados cambiando su estado
     * de 'inactive' a 'active', haciéndolos visibles nuevamente en el sistema.
     *
     * Endpoint: PUT /api/process/{id}/reactivate
     *
     * @param id Identificador del proceso a reactivar
     * @return ProcessDTO del proceso reactivado
     */
    @PutMapping(value = "/{id}/reactivate")
    public ProcessDTO reactivateProcess(@PathVariable Long id) {
        return processService.reactivateProcess(id);
    }

    /**
     * Obtiene todos los procesos inactivos de una empresa.
     *
     * Este endpoint permite visualizar procesos eliminados mediante soft delete
     * para decidir cuáles reactivar.
     *
     * Endpoint: GET /api/process/company/{companyId}/inactive
     *
     * @param companyId Identificador de la empresa
     * @return Lista de procesos inactivos
     */
    @GetMapping(value = "/company/{companyId}/inactive")
    public List<ProcessDTO> getInactiveProcessesByCompany(@PathVariable Long companyId) {
        return processService.getInactiveProcessesByCompany(companyId);
    }

}
