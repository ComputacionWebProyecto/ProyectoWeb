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

import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.service.GatewayService;
import com.proyecto.entrega.service.ProcessService;
import com.proyecto.entrega.exception.UnauthorizedAccessException;
import com.proyecto.entrega.security.SecurityHelper;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    @Autowired
    private GatewayService gatewayService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private SecurityHelper securityHelper;

    @PostMapping()
    public GatewayDTO createGateway(Authentication authentication, @RequestBody GatewayDTO gatewayDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }

        ProcessDTO process = processService.findProcess(gatewayDTO.getProcessId());
        securityHelper.validateCompanyResourceAccess(
                authentication,
                process.getCompany().getId());

        return gatewayService.createGateway(gatewayDTO);
    }

    @PutMapping()
    public GatewayDTO updateGateway(Authentication authentication, @RequestBody GatewayDTO gatewayDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        GatewayDTO existing = gatewayService.findGateway(gatewayDTO.getId());
        securityHelper.validateCompanyResourceAccess(
                authentication,
                existing.getProcess().getCompany().getId());

        ProcessDTO newProcess = processService.findProcess(gatewayDTO.getProcessId());
        securityHelper.validateCompanyResourceAccess(
                authentication,
                newProcess.getCompany().getId());
        return gatewayService.updateGateway(gatewayDTO);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteGateway(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        GatewayDTO gateway = gatewayService.findGateway(id);
        securityHelper.validateCompanyAccess(
                authentication,
                gateway.getProcess().getCompanyId());

        gatewayService.deleteGateway(id);
    }

    @GetMapping(value = "/{id}")
    public GatewayDTO getGateway(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        GatewayDTO gateway = gatewayService.findGateway(id);

        securityHelper.validateCompanyResourceAccess(
                authentication,
                gateway.getProcess().getCompanyId());
        return gatewayService.findGateway(id);
    }

    @GetMapping()
    public List<GatewayDTO> getGateways(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return gatewayService.findGateways();
    }

    /**
     * Obtiene todos los gateways de un proceso específico.
     *
     * Este endpoint permite filtrar gateways por el proceso al que pertenecen,
     * facilitando la visualización de elementos específicos de cada proceso.
     * Solo retorna gateways con status='active'.
     *
     * Endpoint: GET /api/gateway/process/{processId}
     *
     * @param processId Identificador del proceso
     * @return Lista de gateways activos del proceso
     */
    @GetMapping(value = "/process/{processId}")
    public List<GatewayDTO> getGatewaysByProcess(Authentication authentication, @PathVariable Long processId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        
        ProcessDTO process = processService.findProcess(processId);
        securityHelper.validateCompanyAccess(authentication, process.getCompanyId());

        return gatewayService.findGatewaysByProcess(processId);
    }

    /**
     * Obtiene todos los gateways inactivos de un proceso específico.
     *
     * Este endpoint permite visualizar gateways eliminados mediante soft delete
     * de un proceso específico, para decidir cuáles reactivar.
     *
     * Endpoint: GET /api/gateway/process/{processId}/inactive
     *
     * @param processId Identificador del proceso
     * @return Lista de gateways inactivos del proceso
     */
    @GetMapping(value = "/process/{processId}/inactive")
    public List<GatewayDTO> getInactiveGatewaysByProcess(Authentication authentication, @PathVariable Long processId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return gatewayService.findInactiveGatewaysByProcess(processId);
    }

    /**
     * Reactiva un gateway que fue marcado como inactivo mediante soft delete.
     *
     * Este endpoint permite recuperar gateways eliminados cambiando su estado
     * de 'inactive' a 'active', haciéndolos visibles nuevamente en el proceso.
     *
     * Endpoint: PUT /api/gateway/{id}/reactivate
     *
     * @param id Identificador del gateway a reactivar
     * @return GatewayDTO del gateway reactivado
     */
    @PutMapping(value = "/{id}/reactivate")
    public GatewayDTO reactivateGateway(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return gatewayService.reactivateGateway(id);
    }

}
