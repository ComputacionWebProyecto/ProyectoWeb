package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.entity.Edge;
import com.proyecto.entrega.entity.Gateway;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.EdgeRepository;
import com.proyecto.entrega.repository.GatewayRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GatewayService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GatewayRepository gatewayRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private ProcessService processService;

    // Crear Gateway
    public GatewayDTO createGateway(GatewayDTO gatewayDTO) {
        Gateway gateway = modelMapper.map(gatewayDTO, Gateway.class);

        if (gateway.getStatus() == null) {
            gateway.setStatus("active");
        }

        if (gatewayDTO.getProcessId() != null) {
            Process process = processService.findProcessEntity(gatewayDTO.getProcessId());
            gateway.setProcess(process);
        }

        gateway.setX(gatewayDTO.getX());
        gateway.setY(gatewayDTO.getY());

        gateway = gatewayRepository.save(gateway);
        
        System.out.println("✅ Gateway guardado en BD: " + gateway.getId() + 
                          ", tipo: " + gateway.getType() + 
                          ", x: " + gateway.getX() + 
                          ", y: " + gateway.getY());
        
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    // Actualizar Gateway
    public GatewayDTO updateGateway(GatewayDTO gatewayDTO) {
        if (gatewayDTO.getId() == null) {
            throw new IllegalArgumentException("El id del Gateway no puede ser null para actualizar");
        }

        // Verificar que el gateway exista
        Gateway existingGateway = gatewayRepository.findById(gatewayDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + gatewayDTO.getId() + " not found"));


        existingGateway.setType(gatewayDTO.getType());
        existingGateway.setX(gatewayDTO.getX());
        existingGateway.setY(gatewayDTO.getY());

        if (gatewayDTO.getProcessId() != null) {
            Process process = processService.findProcessEntity(gatewayDTO.getProcessId());
            existingGateway.setProcess(process);
        }

        existingGateway = gatewayRepository.save(existingGateway);
        
        System.out.println("Gateway actualizado: " + existingGateway.getId() + 
                          ", x: " + existingGateway.getX() + 
                          ", y: " + existingGateway.getY());
        
        return modelMapper.map(existingGateway, GatewayDTO.class);
    }

    // Buscar Gateway por ID
    public GatewayDTO findGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    public Gateway findGatewayEntity(Long id) {
        return gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));
    }

    // Eliminar Gateway
    public void deleteGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));

        // Eliminar edges conectados en cascada (soft delete)
        List<Edge> connectedEdges = edgeRepository.findByGatewayId(id);
        for (Edge edge : connectedEdges) {
            edge.setStatus("inactive");
            edgeRepository.save(edge);
        }

        System.out.println("Gateway eliminado: ID=" + id + ", Edges afectados: " + connectedEdges.size());

        gateway.setStatus("inactive");
        gatewayRepository.save(gateway);
    }

    // Listar todos los Gateways
    public List<GatewayDTO> findGateways() {
        List<Gateway> gateways = gatewayRepository.findAll();
        
        System.out.println("Gateways en BD: " + gateways.size());
        gateways.forEach(g -> System.out.println("  - ID: " + g.getId() + ", tipo: " + g.getType() + ", x: " + g.getX() + ", y: " + g.getY()));

        return gateways.stream()
                .map(gateway -> modelMapper.map(gateway, GatewayDTO.class))
                .toList();
    }

    /**
     * Obtiene todos los gateways activos de un proceso específico.
     *
     * Este método permite filtrar gateways por el proceso al que pertenecen,
     * facilitando la visualización de elementos específicos de cada proceso.
     * Solo retorna gateways con status='active' debido al filtro @Where de la entidad.
     *
     * @param processId Identificador del proceso
     * @return Lista de GatewayDTO de gateways activos del proceso
     */
    public List<GatewayDTO> findGatewaysByProcess(Long processId) {
        List<Gateway> gateways = gatewayRepository.findByProcessId(processId);

        System.out.println("Gateways activos del proceso " + processId + ": " + gateways.size());

        return gateways.stream()
                .map(gateway -> modelMapper.map(gateway, GatewayDTO.class))
                .toList();
    }

    /**
     * Obtiene todos los gateways inactivos de un proceso específico.
     *
     * Este método permite visualizar gateways que fueron eliminados mediante
     * soft delete de un proceso específico para decidir cuáles reactivar.
     * Utiliza una query personalizada que ignora el filtro @Where de la entidad.
     *
     * @param processId Identificador del proceso
     * @return Lista de GatewayDTO de gateways inactivos del proceso
     */
    public List<GatewayDTO> findInactiveGatewaysByProcess(Long processId) {
        List<Gateway> gateways = gatewayRepository.findByProcessIdAndStatus(processId, "inactive");

        System.out.println("Gateways inactivos del proceso " + processId + ": " + gateways.size());
        gateways.forEach(g -> System.out.println("  - ID: " + g.getId() + ", Type: " + g.getType()));

        return gateways.stream()
                .map(gateway -> modelMapper.map(gateway, GatewayDTO.class))
                .toList();
    }

    /**
     * Reactiva un gateway que fue marcado como inactivo mediante soft delete.
     *
     * Este método permite recuperar gateways eliminados cambiando su estado
     * de 'inactive' a 'active'. El gateway volverá a ser visible en el proceso.
     *
     * El método busca el gateway por id sin importar su estado usando el
     * repositorio directamente para evitar el filtro @Where.
     *
     * @param id Identificador del gateway a reactivar
     * @return GatewayDTO del gateway reactivado
     * @throws EntityNotFoundException si el gateway no existe
     */
    public GatewayDTO reactivateGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));

        gateway.setStatus("active");
        gateway = gatewayRepository.save(gateway);

        System.out.println("Gateway reactivado: ID=" + id + ", Type=" + gateway.getType());

        return modelMapper.map(gateway, GatewayDTO.class);
    }
}