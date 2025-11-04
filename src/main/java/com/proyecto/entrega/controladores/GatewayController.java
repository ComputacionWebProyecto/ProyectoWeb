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
import com.proyecto.entrega.service.GatewayService;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    @Autowired
    private GatewayService gatewayService;

    @PostMapping()
    public GatewayDTO createGateway(@RequestBody GatewayDTO gatewayDTO) {
        return gatewayService.createGateway(gatewayDTO);
    }

    @PutMapping()
    public GatewayDTO updateGateway(@RequestBody GatewayDTO gatewayDTO) {
        return gatewayService.updateGateway(gatewayDTO);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteGateway(@PathVariable Long id) {
        gatewayService.deleteGateway(id);
    }

    @GetMapping(value = "/{id}")
    public GatewayDTO getGateway(@PathVariable Long id) {
        return gatewayService.findGateway(id);
    }

    @GetMapping()
    public List<GatewayDTO> getGateways() {
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
    public List<GatewayDTO> getGatewaysByProcess(@PathVariable Long processId) {
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
    public List<GatewayDTO> getInactiveGatewaysByProcess(@PathVariable Long processId) {
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
    public GatewayDTO reactivateGateway(@PathVariable Long id) {
        return gatewayService.reactivateGateway(id);
    }

}
