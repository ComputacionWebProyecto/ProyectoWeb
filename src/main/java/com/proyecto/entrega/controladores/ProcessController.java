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
import com.proyecto.entrega.exception.UnauthorizedAccessException;
import com.proyecto.entrega.security.SecurityHelper;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/process")
public class ProcessController {
    @Autowired
    private ProcessService processService;

    @Autowired
    private SecurityHelper securityHelper;

    @PostMapping()
    @PreAuthorize("hasRole('administrador')")
    public ProcessDTO createProcess(Authentication authentication, @RequestBody ProcessDTO process) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        securityHelper.validateCompanyAccess(authentication, process.getCompanyId());

        return processService.createProcess(process);
    }

    @PutMapping()
    @PreAuthorize("hasRole('administrador')")
    public ProcessDTO updateProcess(Authentication authentication, @RequestBody ProcessDTO process) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }

        securityHelper.validateCompanyAccess(authentication, process.getCompanyId());

        return processService.updateProcess(process);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('administrador')")
    public void deleteProcess(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        ProcessDTO process = processService.findProcess(id);

        securityHelper.validateCompanyAccess(authentication, process.getCompanyId());

        processService.deleteProcess(id);
    }

    @GetMapping(value = "/{id}")
    public ProcessDTO getProcess(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        ProcessDTO process = processService.findProcess(id);

        securityHelper.validateCompanyResourceAccess(
                authentication,
                process.getCompany().getId());

        return processService.findProcess(id);
    }

    @GetMapping()
    public List<ProcessDTO> getProcesses(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return processService.findProcesses();
    }

    @GetMapping(value = "/company/{id}")
    public List<ProcessDTO> getProcessesByCompany(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        securityHelper.validateCompanyAccess(authentication, id);

        return processService.getProcessesByCompany(id);
    }

    @GetMapping(value = "/company/{companyId}/summary")
    public ResponseEntity<List<ProcessSummaryDTO>> getProcessesSummaryByCompany(Authentication authentication,
            @PathVariable Long companyId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        securityHelper.validateCompanyAccess(authentication, companyId);

        List<ProcessSummaryDTO> summaries = processService.getProcessesSummaryByCompany(companyId);
        return ResponseEntity.ok(summaries);
    }
}
