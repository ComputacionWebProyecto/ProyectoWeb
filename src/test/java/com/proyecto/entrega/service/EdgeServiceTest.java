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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EdgeServiceTest {

    @Mock EdgeRepository edgeRepository;
    @Mock ProcessRepository processRepository;
    @Mock ActivityRepository activityRepository;
    @Mock ModelMapper modelMapper;

    @InjectMocks EdgeService edgeService;

    // ------------ helpers ------------
    private Edge edge(Long id, String desc, String status, Process p, Activity s, Activity d) {
        Edge e = new Edge();
        e.setId(id);
        e.setDescription(desc);
        e.setStatus(status);
        e.setProcess(p);
        e.setActivitySource(s);
        e.setActivityDestiny(d);
        return e;
    }

    private EdgeDTO dto(Long id, String desc, String status, Long processId, Long srcId, Long dstId) {
        EdgeDTO d = new EdgeDTO();
        d.setId(id);
        d.setDescription(desc);
        d.setStatus(status);
        d.setProcessId(processId);
        d.setActivitySourceId(srcId);
        d.setActivityDestinyId(dstId);
        return d;
    }

    // =================== CREATE ===================

    @Test
    void createEdge_ok() {
        EdgeDTO inDto = dto(null, "nuevo enlace", null, 100L, 10L, 11L);

        Process proc = new Process(); proc.setId(100L);
        Activity aSrc = new Activity(); aSrc.setId(10L);
        Activity aDst = new Activity(); aDst.setId(11L);

        Edge mappedFromDto = edge(null, "nuevo enlace", "active", null, null, null); // status por defecto
        Edge persisted      = edge(1L, "nuevo enlace", "active", proc, aSrc, aDst);
        EdgeDTO outDto      = dto(1L, "nuevo enlace", "active", 100L, 10L, 11L);

        when(processRepository.findById(100L)).thenReturn(Optional.of(proc));
        when(activityRepository.findById(10L)).thenReturn(Optional.of(aSrc));
        when(activityRepository.findById(11L)).thenReturn(Optional.of(aDst));

        when(modelMapper.map(inDto, Edge.class)).thenReturn(mappedFromDto);
        when(edgeRepository.save(any(Edge.class))).thenReturn(persisted);
        when(modelMapper.map(persisted, EdgeDTO.class)).thenReturn(outDto);

        EdgeDTO result = edgeService.createEdge(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("nuevo enlace");
        assertThat(result.getStatus()).isEqualTo("active");
        assertThat(result.getProcessId()).isEqualTo(100L);
        assertThat(result.getActivitySourceId()).isEqualTo(10L);
        assertThat(result.getActivityDestinyId()).isEqualTo(11L);

        // validamos que el service inyectó las relaciones antes de guardar
        ArgumentCaptor<Edge> captor = ArgumentCaptor.forClass(Edge.class);
        verify(edgeRepository).save(captor.capture());
        Edge saved = captor.getValue();
        assertThat(saved.getProcess()).isSameAs(proc);
        assertThat(saved.getActivitySource()).isSameAs(aSrc);
        assertThat(saved.getActivityDestiny()).isSameAs(aDst);
    }

    @Test
    void createEdge_processNoExiste_lanzaEntityNotFound() {
        EdgeDTO inDto = dto(null, "x", null, 999L, 10L, 11L);
        when(processRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.createEdge(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("process");

        verify(processRepository).findById(999L);
        verifyNoInteractions(edgeRepository, modelMapper);
    }

    @Test
    void createEdge_sourceNoExiste_lanzaEntityNotFound() {
        EdgeDTO inDto = dto(null, "x", null, 100L, 999L, 11L);
        Process proc = new Process(); proc.setId(100L);
        when(processRepository.findById(100L)).thenReturn(Optional.of(proc));
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.createEdge(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("source");

        verify(activityRepository).findById(999L);
        verifyNoInteractions(edgeRepository, modelMapper);
    }

    @Test
    void createEdge_destinyNoExiste_lanzaEntityNotFound() {
        EdgeDTO inDto = dto(null, "x", null, 100L, 10L, 999L);
        Process proc = new Process(); proc.setId(100L);
        Activity src = new Activity(); src.setId(10L);
        when(processRepository.findById(100L)).thenReturn(Optional.of(proc));
        when(activityRepository.findById(10L)).thenReturn(Optional.of(src));
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.createEdge(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("destiny");

        verify(activityRepository).findById(999L);
        verifyNoInteractions(edgeRepository, modelMapper);
    }

    // =================== UPDATE ===================

    @Test
    void updateEdge_ok() {
        EdgeDTO inDto = dto(10L, "editado", "active", 200L, 20L, 21L);

        Process proc = new Process(); proc.setId(200L);
        Activity aSrc = new Activity(); aSrc.setId(20L);
        Activity aDst = new Activity(); aDst.setId(21L);

        Edge existing = edge(10L, "viejo", "active", null, null, null);
        Edge saved    = edge(10L, "editado", "active", proc, aSrc, aDst);
        EdgeDTO outDto = dto(10L, "editado", "active", 200L, 20L, 21L);

        when(edgeRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(processRepository.findById(200L)).thenReturn(Optional.of(proc));
        when(activityRepository.findById(20L)).thenReturn(Optional.of(aSrc));
        when(activityRepository.findById(21L)).thenReturn(Optional.of(aDst));
        when(edgeRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, EdgeDTO.class)).thenReturn(outDto);

        EdgeDTO result = edgeService.updateEdge(inDto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("editado");
        assertThat(result.getStatus()).isEqualTo("active");
        assertThat(result.getProcessId()).isEqualTo(200L);

        assertThat(existing.getDescription()).isEqualTo("editado");
        assertThat(existing.getProcess()).isSameAs(proc);
        assertThat(existing.getActivitySource()).isSameAs(aSrc);
        assertThat(existing.getActivityDestiny()).isSameAs(aDst);

        verify(edgeRepository).save(existing);
    }

    @Test
    void updateEdge_noExiste_lanzaEntityNotFound() {
        EdgeDTO inDto = dto(77L, "x", "active", 1L, 2L, 3L);
        when(edgeRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.updateEdge(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("77");

        verify(edgeRepository).findById(77L);
        verifyNoMoreInteractions(edgeRepository);
    }

    // =================== FIND ===================

    @Test
    void findEdge_found() {
        Edge entity = edge(2L, "flujo principal", "active", null, null, null);
        EdgeDTO dto = dto(2L, "flujo principal", "active", 50L, 5L, 6L);

        when(edgeRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, EdgeDTO.class)).thenReturn(dto);

        EdgeDTO result = edgeService.findEdge(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getDescription()).isEqualTo("flujo principal");
        assertThat(result.getStatus()).isEqualTo("active");
        assertThat(result.getProcessId()).isEqualTo(50L);
    }

    @Test
    void findEdge_notFound_lanzaEntityNotFound() {
        when(edgeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.findEdge(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(edgeRepository).findById(999L);
        verifyNoMoreInteractions(edgeRepository);
    }

    // =================== DELETE (soft) ===================

    @Test
    void deleteEdge_ok_softDelete() {
        Edge existing = edge(7L, "a borrar", "active", null, null, null);
        when(edgeRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(edgeRepository.save(existing)).thenReturn(existing);

        edgeService.deleteEdge(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");
        ArgumentCaptor<Edge> captor = ArgumentCaptor.forClass(Edge.class);
        verify(edgeRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("inactive");
    }

    @Test
    void deleteEdge_notFound_lanzaEntityNotFound() {
        when(edgeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.deleteEdge(404L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("404");

        verify(edgeRepository).findById(404L);
        verifyNoMoreInteractions(edgeRepository);
    }

    // =================== LIST ===================

    @Test
    void findEdges_ok() {
        Edge a = edge(1L, "A→B", "active", null, null, null);
        when(edgeRepository.findAll()).thenReturn(List.of(a));

        EdgeDTO dto = dto(1L, "A→B", "active", 10L, 2L, 3L);
        when(modelMapper.map(a, EdgeDTO.class)).thenReturn(dto);

        List<EdgeDTO> result = edgeService.findEdges();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("A→B");
        assertThat(result.get(0).getStatus()).isEqualTo("active");
        assertThat(result.get(0).getProcessId()).isEqualTo(10L);

        verify(edgeRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Edge.class), eq(EdgeDTO.class));
    }
}
