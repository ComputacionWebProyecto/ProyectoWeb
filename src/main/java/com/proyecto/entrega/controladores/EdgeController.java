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

import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.service.ActivityService;
import com.proyecto.entrega.service.EdgeService;
import com.proyecto.entrega.service.GatewayService;
import com.proyecto.entrega.service.ProcessService;
import com.proyecto.entrega.exception.UnauthorizedAccessException;
import com.proyecto.entrega.security.SecurityHelper;

import org.springframework.security.core.Authentication;

/**
 * EdgeController - REST API para gestión de edges
 *
 * Soporta TODAS las conexiones: A→A, A→G, G→A, G→G
 * Version: 2.0 (con soporte tipado)
 */
@RestController
@RequestMapping("/api/edge")
public class EdgeController {

    @Autowired
    private EdgeService edgeService;

    @Autowired
    private SecurityHelper securityHelper;

    @Autowired
    private ProcessService processService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private GatewayService gatewayService;

    private void validateNodeAccess(Authentication authentication, EdgeDTO edge, Long userCompanyId) {
        // Verificar formato tipado (nuevo)
        if (edge.getFromType() != null && edge.getToType() != null) {
            validateTypedNode(edge.getFromType(), edge.getFromId(), userCompanyId, "origen");
            validateTypedNode(edge.getToType(), edge.getToId(), userCompanyId, "destino");
        }
        // Verificar formato legacy (activity -> activity)
        else if (edge.getActivitySourceId() != null && edge.getActivityDestinyId() != null) {
            validateActivityNode(edge.getActivitySourceId(), userCompanyId, "origen");
            validateActivityNode(edge.getActivityDestinyId(), userCompanyId, "destino");
        }
    }

    private void validateTypedNode(String nodeType, Long nodeId, Long userCompanyId, String position) {
        if (nodeId == null) {
            return; // Si no hay ID, el servicio manejará el error
        }

        if ("activity".equals(nodeType)) {
            validateActivityNode(nodeId, userCompanyId, position);
        } else if ("gateway".equals(nodeType)) {
            validateGatewayNode(nodeId, userCompanyId, position);
        }
    }

    private void validateActivityNode(Long activityId, Long userCompanyId, String position) {
        ActivityDTO activity = activityService.findActivity(activityId);
        if (!activity.getProcess().getCompany().getId().equals(userCompanyId)) {
            throw new UnauthorizedAccessException(
                    "No tienes acceso a la actividad de " + position + " (ID: " + activityId + ")");
        }
    }

    private void validateGatewayNode(Long gatewayId, Long userCompanyId, String position) {
        GatewayDTO gateway = gatewayService.findGateway(gatewayId);
        if (!gateway.getProcess().getCompany().getId().equals(userCompanyId)) {
            throw new UnauthorizedAccessException(
                    "No tienes acceso al gateway de " + position + " (ID: " + gatewayId + ")");
        }
    }

    @PostMapping()
    public EdgeDTO createEdge(Authentication authentication, @RequestBody EdgeDTO edgeDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }

        // Obtener el proceso y validar acceso a la compañía
        ProcessDTO process = processService.findProcess(edgeDTO.getProcessId());
        Long userCompanyId = securityHelper.getUserCompanyId(authentication);

        if (!process.getCompany().getId().equals(userCompanyId)) {
            throw new UnauthorizedAccessException("No tienes acceso a este proceso");
        }

        // Validar acceso a los nodos conectados (source y destiny)
        validateNodeAccess(authentication, edgeDTO, userCompanyId);

        return edgeService.createEdge(edgeDTO);
    }

    @PutMapping()
    public EdgeDTO updateEdge(Authentication authentication, @RequestBody EdgeDTO edgeDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        // Validar acceso al edge existente
        EdgeDTO existing = edgeService.findEdge(edgeDTO.getId());
        securityHelper.validateCompanyResourceAccess(
                authentication,
                existing.getProcess().getCompany().getId());

        // Validar acceso al nuevo proceso (si cambió)
        ProcessDTO newProcess = processService.findProcess(edgeDTO.getProcessId());
        securityHelper.validateCompanyResourceAccess(
                authentication,
                newProcess.getCompany().getId());

        return edgeService.updateEdge(edgeDTO);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEdge(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        EdgeDTO edge = edgeService.findEdge(id);
        securityHelper.validateCompanyAccess(
                authentication,
                edge.getProcess().getCompanyId());

        edgeService.deleteEdge(id);
    }

    @GetMapping(value = "/{id}")
    public EdgeDTO getEdge(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        EdgeDTO edge = edgeService.findEdge(id);

        securityHelper.validateCompanyResourceAccess(
                authentication,
                edge.getProcess().getCompanyId());
        return edgeService.findEdge(id);
    }

    @GetMapping()
    public List<EdgeDTO> getEdges(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return edgeService.findEdges();
    }

    /**
     * Obtiene todos los edges de un proceso específico.
     *
     * Este endpoint permite filtrar edges por el proceso al que pertenecen,
     * facilitando la visualización de conexiones específicas de cada proceso.
     * Solo retorna edges con status='active'.
     *
     * Endpoint: GET /api/edge/process/{processId}
     *
     * @param processId Identificador del proceso
     * @return Lista de edges activos del proceso
     */
    @GetMapping(value = "/process/{processId}")
    public List<EdgeDTO> getEdgesByProcess(Authentication authentication, @PathVariable Long processId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        ProcessDTO process = processService.findProcess(processId);
        securityHelper.validateCompanyAccess(authentication, process.getCompanyId());

        return edgeService.findEdgesByProcess(processId);
    }

    /**
     * Obtiene todos los edges inactivos de un proceso específico.
     *
     * Este endpoint permite visualizar edges eliminados mediante soft delete
     * de un proceso específico, para decidir cuáles reactivar.
     *
     * Endpoint: GET /api/edge/process/{processId}/inactive
     *
     * @param processId Identificador del proceso
     * @return Lista de edges inactivos del proceso
     */
    @GetMapping(value = "/process/{processId}/inactive")
    public List<EdgeDTO> getInactiveEdgesByProcess(Authentication authentication, @PathVariable Long processId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return edgeService.findInactiveEdgesByProcess(processId);
    }

    /**
     * Reactiva un edge que fue marcado como inactivo mediante soft delete.
     *
     * Este endpoint permite recuperar edges eliminados cambiando su estado
     * de 'inactive' a 'active', haciéndolos visibles nuevamente en el proceso.
     *
     * Endpoint: PUT /api/edge/{id}/reactivate
     *
     * @param id Identificador del edge a reactivar
     * @return EdgeDTO del edge reactivado
     */
    @PutMapping(value = "/{id}/reactivate")
    public EdgeDTO reactivateEdge(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return edgeService.reactivateEdge(id);
    }

}
