package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.dto.ProcessSummaryDTO;
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

    //Para listar procesos
    public List<ProcessSummaryDTO> getProcessesSummaryByCompany(Long id) {
    List<Process> processes = processRepository.findByCompanyId(id);
    
    System.out.println("Procesos resumidos enviados al frontend:");
    processes.forEach(p -> System.out.println(
        "ID: " + p.getId() + ", Name: " + p.getName() + ", Description: " + p.getDescription()
    ));
    
    return processes.stream()
            .map(process -> new ProcessSummaryDTO(
                    process.getId(),
                    process.getName(),
                    process.getDescription()
            ))
            .toList();
    }

    /**
     * Reactiva un proceso que fue marcado como inactivo mediante soft delete.
     *
     * Este método permite recuperar procesos eliminados cambiando su estado
     * de 'inactive' a 'active'. El proceso y todos sus elementos asociados
     * volverán a ser visibles en el sistema.
     *
     * El método busca el proceso por id sin importar su estado (activo o inactivo)
     * usando el repositorio directamente para evitar el filtro @Where.
     *
     * @param id Identificador del proceso a reactivar
     * @return ProcessDTO del proceso reactivado
     * @throws EntityNotFoundException si el proceso no existe
     */
    public ProcessDTO reactivateProcess(Long id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Process " + id + " not found"));

        process.setStatus("active");
        process = processRepository.save(process);

        System.out.println("Proceso reactivado: ID=" + id + ", Name=" + process.getName());

        return modelMapper.map(process, ProcessDTO.class);
    }

    /**
     * Obtiene todos los procesos inactivos de una empresa específica.
     *
     * Este método permite visualizar procesos que fueron eliminados mediante
     * soft delete para decidir cuáles reactivar. Utiliza una query personalizada
     * que ignora el filtro @Where(clause = "status = 'active'") de la entidad.
     *
     * @param companyId Identificador de la empresa
     * @return Lista de ProcessDTO de procesos inactivos
     */
    public List<ProcessDTO> getInactiveProcessesByCompany(Long companyId) {
        List<Process> processes = processRepository.findByCompanyIdAndStatus(companyId, "inactive");

        System.out.println("Procesos inactivos encontrados: " + processes.size());
        processes.forEach(p -> System.out.println("  - ID: " + p.getId() + ", Name: " + p.getName()));

        return processes.stream()
                .map(process -> modelMapper.map(process, ProcessDTO.class))
                .toList();
    }

}
