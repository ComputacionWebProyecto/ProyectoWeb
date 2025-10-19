package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.entity.Edge;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.ActivityRepository;
import com.proyecto.entrega.repository.EdgeRepository;
import com.proyecto.entrega.repository.ProcessRepository;
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

    @Mock EdgeRepository edgeRepository;
    @Mock ProcessRepository processRepository;
    @Mock ActivityRepository activityRepository;
    @Mock ModelMapper modelMapper;

    @InjectMocks EdgeService edgeService;

    @Test
    void createEdge_ok() {
        // DTO con IDs de relaciones
        EdgeDTO inDto = new EdgeDTO(null, "nuevo enlace", 100L, 10L, 11L);

        // Entidades relacionadas encontradas
        Process proc = new Process(); proc.setId(100L);
        Activity aSrc = new Activity(); aSrc.setId(10L);
        Activity aDst = new Activity(); aDst.setId(11L);

        // Entity antes/después de guardar
        Edge entityBefore = new Edge(null, "nuevo enlace", "active", proc, aSrc, aDst);
        Edge entitySaved  = new Edge(1L,   "nuevo enlace", "active", proc, aSrc, aDst);

        EdgeDTO outDto = new EdgeDTO(1L, "nuevo enlace", 100L, 10L, 11L);

        // Stubs de repos relacionados
        when(processRepository.findById(100L)).thenReturn(Optional.of(proc));
        when(activityRepository.findById(10L)).thenReturn(Optional.of(aSrc));
        when(activityRepository.findById(11L)).thenReturn(Optional.of(aDst));

        // Si tu servicio mapea desde el DTO:
        when(modelMapper.map(inDto, Edge.class)).thenReturn(entityBefore);

        when(edgeRepository.save(any(Edge.class))).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, EdgeDTO.class)).thenReturn(outDto);

        // Act
        EdgeDTO result = edgeService.createEdge(inDto);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("nuevo enlace");
        assertThat(result.getProcessId()).isEqualTo(100L);
        assertThat(result.getActivitySourceId()).isEqualTo(10L);
        assertThat(result.getActivityDestinyId()).isEqualTo(11L);

        verify(processRepository).findById(100L);
        verify(activityRepository).findById(10L);
        verify(activityRepository).findById(11L);
        verify(edgeRepository).save(any(Edge.class));
    }

    @Test
    void updateEdge_ok() {
        // Entrada
        EdgeDTO inDto = new EdgeDTO(10L, "editado", 200L, 20L, 21L);

        // Entidades relacionadas
        Process proc = new Process(); proc.setId(200L);
        Activity aSrc = new Activity(); aSrc.setId(20L);
        Activity aDst = new Activity(); aDst.setId(21L);

        // Edge existente en BD
        Edge existing = new Edge(10L, "viejo", "active", null, null, null);

        // Edge devuelto al guardar
        Edge saved = new Edge(10L, "editado", "active", proc, aSrc, aDst);

        EdgeDTO outDto = new EdgeDTO(10L, "editado", 200L, 20L, 21L);

        when(edgeRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(processRepository.findById(200L)).thenReturn(Optional.of(proc));
        when(activityRepository.findById(20L)).thenReturn(Optional.of(aSrc));
        when(activityRepository.findById(21L)).thenReturn(Optional.of(aDst));

        // Si en tu servicio usas modelMapper.map(entity, EdgeDTO.class) al final:
        when(edgeRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, EdgeDTO.class)).thenReturn(outDto);

        // Act
        EdgeDTO result = edgeService.updateEdge(inDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("editado");
        assertThat(result.getProcessId()).isEqualTo(200L);

        verify(edgeRepository).findById(10L);
        verify(edgeRepository).save(existing);
    }

    @Test
    void findEdge_found() {
        Edge entity = new Edge(2L, "flujo principal", "active", null, null, null);
        EdgeDTO dto  = new EdgeDTO(2L, "flujo principal", 50L, 5L, 6L);

        when(edgeRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, EdgeDTO.class)).thenReturn(dto);

        EdgeDTO result = edgeService.findEdge(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getDescription()).isEqualTo("flujo principal");
        assertThat(result.getProcessId()).isEqualTo(50L);
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
        // Ahora el servicio hace soft delete: primero findById, luego setStatus("inactive") y save.
        Edge existing = new Edge(7L, "a borrar", "active", null, null, null);
        when(edgeRepository.findById(7L)).thenReturn(Optional.of(existing));

        edgeService.deleteEdge(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");
        verify(edgeRepository).findById(7L);
        verify(edgeRepository).save(existing);
    }

    @Test
    void findEdges_ok() {
        Edge a = new Edge(1L, "A→B", "active", null, null, null);
        when(edgeRepository.findAll()).thenReturn(List.of(a));

        EdgeDTO dto = new EdgeDTO(1L, "A→B", 10L, 2L, 3L);
        when(modelMapper.map(a, EdgeDTO.class)).thenReturn(dto);

        List<EdgeDTO> result = edgeService.findEdges();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("A→B");
        assertThat(result.get(0).getProcessId()).isEqualTo(10L);
        verify(edgeRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Edge.class), eq(EdgeDTO.class));
    }
}
