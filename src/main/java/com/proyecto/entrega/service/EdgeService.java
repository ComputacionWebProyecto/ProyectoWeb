package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.entity.Edge;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.entity.Gateway;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.EdgeRepository;

/**
 * EdgeService - Servicio para gestión de conexiones entre nodos
 *
 * Soporta TODAS las combinaciones de conexiones:
 * - Activity → Activity
 * - Activity → Gateway
 * - Gateway → Activity
 * - Gateway → Gateway
 *
 * NORMALIZACIÓN AUTOMÁTICA:
 * Acepta dos formatos de entrada y los normaliza automáticamente:
 * 1. Formato legacy: activitySourceId, activityDestinyId (solo A→A)
 * 2. Formato tipado: fromType, fromId, toType, toId (todas las combinaciones)
 *
 * COMPATIBILIDAD:
 * Todos los edges se guardan con ambos formatos cuando es aplicable,
 * garantizando máxima compatibilidad con código existente.
 */
@Service
public class EdgeService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private ProcessService processService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private GatewayService gatewayService;

    /**
     * Crea un nuevo Edge normalizando automáticamente el formato de entrada.
     *
     * SOPORTA DOS FORMATOS:
     *
     * 1. FORMATO LEGACY (Activity→Activity):
     * - activitySourceId, activityDestinyId
     * → Se convierte automáticamente a fromType='activity', toType='activity'
     *
     * 2. FORMATO TIPADO (Todas las conexiones):
     * - fromType, fromId, toType, toId
     * → Si es A→A, también puebla activitySource y activityDestiny para
     * compatibilidad
     *
     * EJEMPLOS:
     *
     * Activity→Activity (legacy):
     * { activitySourceId: 1, activityDestinyId: 2 }
     * → fromType='activity', fromId=1, toType='activity', toId=2
     *
     * Activity→Gateway (tipado):
     * { fromType: 'activity', fromId: 1, toType: 'gateway', toId: 5 }
     *
     * Gateway→Activity (tipado):
     * { fromType: 'gateway', fromId: 5, toType: 'activity', toId: 2 }
     *
     * Gateway→Gateway (tipado):
     * { fromType: 'gateway', fromId: 3, toType: 'gateway', toId: 5 }
     */
    public EdgeDTO createEdge(EdgeDTO edgeDTO) {
        Process process = processService.findProcessEntity(edgeDTO.getProcessId());

        Edge edge = new Edge();
        edge.setLabel(edgeDTO.getLabel());
        edge.setDescription(edgeDTO.getDescription());
        edge.setStatus("active");
        edge.setProcess(process);

        normalizeEndpoints(edge, edgeDTO);

        edge = edgeRepository.save(edge);

        System.out.println("Edge creado: ID=" + edge.getId() +
                ", " + edge.getFromType() + ":" + edge.getFromId() +
                " → " + edge.getToType() + ":" + edge.getToId());

        return modelMapper.map(edge, EdgeDTO.class);
    }

    /**
     * Actualiza un Edge existente normalizando los endpoints.
     */
    public EdgeDTO updateEdge(EdgeDTO edgeDTO) {
        Edge existing = edgeRepository.findById(edgeDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Edge", "id", edgeDTO.getId()));

        Process process = processService.findProcessEntity(edgeDTO.getProcessId());

        existing.setLabel(edgeDTO.getLabel());
        existing.setDescription(edgeDTO.getDescription());
        existing.setProcess(process);

        normalizeEndpoints(existing, edgeDTO);

        existing = edgeRepository.save(existing);
        return modelMapper.map(existing, EdgeDTO.class);
    }

    /**
     * Normaliza los endpoints de un Edge desde un DTO.
     *
     * LÓGICA DE NORMALIZACIÓN:
     *
     * 1. Si el DTO tiene fromType y toType (formato tipado):
     * - Usar esos valores directamente
     * - Si es Activity→Activity, también poblar activitySource/Destiny
     *
     * 2. Si el DTO tiene activitySourceId y activityDestinyId (formato legacy):
     * - Inferir fromType='activity', toType='activity'
     * - Poblar fromId=activitySourceId, toId=activityDestinyId
     * - Poblar activitySource/Destiny
     *
     * COMPATIBILIDAD:
     * Garantiza que todos los edges tengan AMBOS formatos para máxima
     * compatibilidad.
     */
    private void normalizeEndpoints(Edge edge, EdgeDTO dto) {
        boolean hasTipado = dto.getFromType() != null && dto.getToType() != null;
        boolean hasLegacy = dto.getActivitySourceId() != null && dto.getActivityDestinyId() != null;

        if (hasTipado) {
            edge.setFromType(dto.getFromType());
            edge.setFromId(dto.getFromId());
            edge.setToType(dto.getToType());
            edge.setToId(dto.getToId());

            if ("activity".equals(dto.getFromType()) && "activity".equals(dto.getToType())) {
                Activity source = activityService.findActivityEntity(dto.getFromId());
                Activity destiny = activityService.findActivityEntity(dto.getToId());
                edge.setActivitySource(source);
                edge.setActivityDestiny(destiny);
            } else {
                edge.setActivitySource(null);
                edge.setActivityDestiny(null);
            }

        } else if (hasLegacy) {
            edge.setFromType("activity");
            edge.setFromId(dto.getActivitySourceId());
            edge.setToType("activity");
            edge.setToId(dto.getActivityDestinyId());

            Activity source = activityService.findActivityEntity(dto.getActivitySourceId());
            Activity destiny = activityService.findActivityEntity(dto.getActivityDestinyId());
            edge.setActivitySource(source);
            edge.setActivityDestiny(destiny);

        } else {
            throw new ValidationException(
                    "El edge debe tener endpoints definidos: usar (fromType/fromId/toType/toId) " +
                            "o (activitySourceId/activityDestinyId)");
        }
    }

    public EdgeDTO findEdge(Long id) {
        Edge edge = edgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edge", "id", id));
        return modelMapper.map(edge, EdgeDTO.class);
    }

    public Edge findEdgeEntity(Long id) {
        return edgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edge", "id", id));
    }

    public void deleteEdge(Long id) {
        Edge edge = edgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edge", "id", id));
        edge.setStatus("inactive");
        edgeRepository.save(edge);
    }

    public List<EdgeDTO> findEdges() {
        List<Edge> edges = edgeRepository.findAll();
        return edges.stream()
                .map(edge -> modelMapper.map(edge, EdgeDTO.class))
                .toList();
    }

    /**
     * Obtiene todos los edges activos de un proceso específico.
     *
     * Este método permite filtrar edges por el proceso al que pertenecen,
     * facilitando la visualización de conexiones específicas de cada proceso.
     * Solo retorna edges con status='active' debido al filtro @Where de la entidad.
     */
    public List<EdgeDTO> findEdgesByProcess(Long processId) {
        List<Edge> edges = edgeRepository.findByProcessId(processId);
        System.out.println("Edges activos del proceso " + processId + ": " + edges.size());
        return edges.stream()
                .map(edge -> modelMapper.map(edge, EdgeDTO.class))
                .toList();
    }

    /**
     * Obtiene todos los edges inactivos de un proceso específico.
     *
     * Este método permite visualizar edges eliminados mediante soft delete
     * de un proceso específico, útil para decidir cuáles reactivar.
     */
    public List<EdgeDTO> findInactiveEdgesByProcess(Long processId) {
        List<Edge> edges = edgeRepository.findByProcessIdAndStatus(processId, "inactive");
        System.out.println("Edges inactivos del proceso " + processId + ": " + edges.size());
        return edges.stream()
                .map(edge -> modelMapper.map(edge, EdgeDTO.class))
                .toList();
    }

    /**
     * Busca edges que tengan una activity específica como origen o destino.
     */
    public List<Edge> findByActivityId(Long activityId) {
        return edgeRepository.findByActivityId(activityId);
    }

    /**
     * Busca edges que tengan un gateway específico como origen o destino.
     */
    public List<Edge> findByGatewayId(Long gatewayId) {
        return edgeRepository.findByGatewayId(gatewayId);
    }

    /**
     * Reactiva un edge marcado como inactive.
     */
    public EdgeDTO reactivateEdge(Long id) {
        Edge edge = edgeRepository.findByIdIgnoreStatus(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edge", "id", id));
        edge.setStatus("active");
        edge = edgeRepository.save(edge);
        return modelMapper.map(edge, EdgeDTO.class);
    }
}
