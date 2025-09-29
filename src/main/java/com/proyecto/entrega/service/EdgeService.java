package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.entity.Edge;
import com.proyecto.entrega.repository.EdgeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EdgeService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EdgeRepository edgeRepository;

    public EdgeDTO createEdge(EdgeDTO edgeDTO) {
        Edge edge = modelMapper.map(edgeDTO, Edge.class);
        edge = edgeRepository.save(edge);
        return modelMapper.map(edge, EdgeDTO.class);
    }

    public EdgeDTO updateEdge(EdgeDTO edgeDTO) {
        Edge edge = modelMapper.map(edgeDTO, Edge.class);
        edge = edgeRepository.save(edge);
        return modelMapper.map(edge, EdgeDTO.class);
    }

    public EdgeDTO findEdge(Long id) {
        Edge edge = edgeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Edge " + id + " not found"));
        return modelMapper.map(edge, EdgeDTO.class);
    }   

    public void deleteEdge(Long id) {
        edgeRepository.deleteById(id);
    }

    public List<EdgeDTO> findEdges() {
        List<Edge> edges = edgeRepository.findAll();
        return edges.stream()
                .map(edge -> modelMapper.map(edge, EdgeDTO.class))
                .toList();
    }

}
