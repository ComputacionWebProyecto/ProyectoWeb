package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.entity.Edge;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.EdgeRepository;
import com.proyecto.entrega.repository.ActivityRepository;
import com.proyecto.entrega.repository.ProcessRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EdgeService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ActivityRepository activityRepository;

    public EdgeDTO createEdge(EdgeDTO edgeDTO) {
        validateIds(edgeDTO);

        Process process = processRepository.findById(edgeDTO.getProcessId())
                .orElseThrow(() -> new EntityNotFoundException("Process not found"));

        Activity source = activityRepository.findById(edgeDTO.getActivitySourceId())
                .orElseThrow(() -> new EntityNotFoundException("Activity Source not found"));

        Activity destiny = activityRepository.findById(edgeDTO.getActivityDestinyId())
                .orElseThrow(() -> new EntityNotFoundException("Activity Destiny not found"));

        Edge edge = modelMapper.map(edgeDTO, Edge.class);
        edge.setProcess(process);
        edge.setActivitySource(source);
        edge.setActivityDestiny(destiny);

        edge = edgeRepository.save(edge);
        return modelMapper.map(edge, EdgeDTO.class);
    }

    public EdgeDTO updateEdge(EdgeDTO edgeDTO) {
        validateIds(edgeDTO);

        Edge existing = edgeRepository.findById(edgeDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Edge " + edgeDTO.getId() + " not found"));

        Process process = processRepository.findById(edgeDTO.getProcessId())
                .orElseThrow(() -> new EntityNotFoundException("Process not found"));

        Activity source = activityRepository.findById(edgeDTO.getActivitySourceId())
                .orElseThrow(() -> new EntityNotFoundException("Activity Source not found"));

        Activity destiny = activityRepository.findById(edgeDTO.getActivityDestinyId())
                .orElseThrow(() -> new EntityNotFoundException("Activity Destiny not found"));

        // Actualizamos campos
        existing.setDescription(edgeDTO.getDescription());
        existing.setProcess(process);
        existing.setActivitySource(source);
        existing.setActivityDestiny(destiny);

        existing = edgeRepository.save(existing);
        return modelMapper.map(existing, EdgeDTO.class);
    }

    public EdgeDTO findEdge(Long id) {
        Edge edge = edgeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Edge " + id + " not found"));
        return modelMapper.map(edge, EdgeDTO.class);
    }

    public void deleteEdge(Long id) {
        Edge edge = edgeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Edge " + id + " not found"));
        edge.setStatus("inactive");
        edgeRepository.save(edge);
    }

    public List<EdgeDTO> findEdges() {
        List<Edge> edges = edgeRepository.findAll();
        return edges.stream()
                .map(edge -> modelMapper.map(edge, EdgeDTO.class))
                .toList();
    }

    private void validateIds(EdgeDTO edgeDTO) {
        if (edgeDTO.getProcessId() == null) {
            throw new IllegalArgumentException("Process ID cannot be null");
        }
        if (edgeDTO.getActivitySourceId() == null) {
            throw new IllegalArgumentException("Activity Source ID cannot be null");
        }
        if (edgeDTO.getActivityDestinyId() == null) {
            throw new IllegalArgumentException("Activity Destiny ID cannot be null");
        }
    }
}
