package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.entity.Edge;
import com.proyecto.entrega.repository.EdgeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EdgeServiceTest {

    @Mock
    EdgeRepository edgeRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    EdgeService edgeService;

    @Test
    void createEdge_ok() {
        EdgeDTO inDto = new EdgeDTO(null, "nuevo enlace", "active");
        Edge entityBefore = new Edge(null, "nuevo enlace", "active", null, null, null);
        Edge entitySaved  = new Edge(1L,   "nuevo enlace", "active", null, null, null);
        EdgeDTO outDto    = new EdgeDTO(1L, "nuevo enlace", "active");

        when(modelMapper.map(inDto, Edge.class)).thenReturn(entityBefore);
        when(edgeRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, EdgeDTO.class)).thenReturn(outDto);

        EdgeDTO result = edgeService.createEdge(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("nuevo enlace");
        verify(edgeRepository).save(entityBefore);
    }

    @Test
    void updateEdge_ok() {
        EdgeDTO inDto = new EdgeDTO(10L, "editado", "inactive");
        Edge entity = new Edge(10L, "editado", "inactive", null, null, null);

        when(modelMapper.map(inDto, Edge.class)).thenReturn(entity);
        when(edgeRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, EdgeDTO.class)).thenReturn(inDto);

        EdgeDTO result = edgeService.updateEdge(inDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo("inactive");
        verify(edgeRepository).save(entity);
    }

    @Test
    void findEdge_found() {
        Edge entity = new Edge(2L, "flujo principal", "active", null, null, null);
        EdgeDTO dto = new EdgeDTO(2L, "flujo principal", "active");

        when(edgeRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, EdgeDTO.class)).thenReturn(dto);

        EdgeDTO result = edgeService.findEdge(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getDescription()).isEqualTo("flujo principal");
    }

    @Test
    void findEdge_notFound_throwsEntityNotFound() {
        when(edgeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.findEdge(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(edgeRepository).findById(999L);
        verifyNoMoreInteractions(edgeRepository);
    }

    @Test
    void deleteEdge_ok() {
        edgeService.deleteEdge(7L);
        verify(edgeRepository).deleteById(7L);
    }

    @Test
    void findEdges_ok() {
        Edge a = new Edge(1L, "A→B", "active", null, null, null);
        when(edgeRepository.findAll()).thenReturn(List.of(a));

        EdgeDTO dto = new EdgeDTO(1L, "A→B", "active");
        when(modelMapper.map(a, EdgeDTO.class)).thenReturn(dto);

        List<EdgeDTO> result = edgeService.findEdges();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("A→B");
        verify(edgeRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Edge.class), eq(EdgeDTO.class));
    }
}
