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

import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.service.EdgeService;
import com.proyecto.entrega.exception.UnauthorizedAccessException;
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

    @PostMapping()
    public EdgeDTO createEdge(Authentication authentication, @RequestBody EdgeDTO edgeDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return edgeService.createEdge(edgeDTO);
    }

    @PutMapping()
    public EdgeDTO updateEdge(Authentication authentication, @RequestBody EdgeDTO edgeDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return edgeService.updateEdge(edgeDTO);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEdge(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        edgeService.deleteEdge(id);
    }

    @GetMapping(value = "/{id}")
    public EdgeDTO getEdge(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
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
