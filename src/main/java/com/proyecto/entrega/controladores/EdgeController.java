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
    public EdgeDTO createEdge(@RequestBody EdgeDTO edgeDTO) {
        return edgeService.createEdge(edgeDTO);
    }

    @PutMapping()
    public EdgeDTO updateEdge(@RequestBody EdgeDTO edgeDTO) {
        return edgeService.updateEdge(edgeDTO);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEdge(@PathVariable Long id) {
        edgeService.deleteEdge(id);
    }

    @GetMapping(value = "/{id}")
    public EdgeDTO getEdge(@PathVariable Long id) {
        return edgeService.findEdge(id);
    }

    @GetMapping()
    public List<EdgeDTO> getEdges() {
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
    public List<EdgeDTO> getEdgesByProcess(@PathVariable Long processId) {
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
    public List<EdgeDTO> getInactiveEdgesByProcess(@PathVariable Long processId) {
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
    public EdgeDTO reactivateEdge(@PathVariable Long id) {
        return edgeService.reactivateEdge(id);
    }

}
