package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.entity.Edge;
import com.proyecto.entrega.entity.Gateway;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.EdgeRepository;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EdgeServiceTest {

    @Mock EdgeRepository edgeRepository;
    @Mock ModelMapper modelMapper;
    @Mock ProcessService processService;
    @Mock ActivityService activityService;
    @Mock GatewayService gatewayService;

    @InjectMocks EdgeService edgeService;

    // ===== Helpers =====
    private EdgeDTO dtoLegacy(Long id, String desc, Long processId, Long srcId, Long dstId) {
        EdgeDTO d = new EdgeDTO();
        d.setId(id);
        d.setDescription(desc);
        d.setProcessId(processId);
        d.setActivitySourceId(srcId);
        d.setActivityDestinyId(dstId);
        return d;
    }

    private EdgeDTO dtoTyped(Long id, String desc, Long processId,
                             String fromType, Long fromId,
                             String toType, Long toId) {
        EdgeDTO d = new EdgeDTO();
        d.setId(id);
        d.setDescription(desc);
        d.setProcessId(processId);
        d.setFromType(fromType);
        d.setFromId(fromId);
        d.setToType(toType);
        d.setToId(toId);
        return d;
    }

    private Edge edge(Long id, String desc, String status, Process p, Activity a1, Activity a2) {
        Edge e = new Edge();
        e.setId(id);
        e.setDescription(desc);
        e.setStatus(status);
        e.setProcess(p);
        e.setActivitySource(a1);
        e.setActivityDestiny(a2);
        return e;
    }

    

    @Test
    void createEdge_legacy_ok() {
        EdgeDTO inDto = dtoLegacy(null, "nuevo", 10L, 1L, 2L);

        Process proc = new Process(); proc.setId(10L);
        Activity aSrc = new Activity(); aSrc.setId(1L);
        Activity aDst = new Activity(); aDst.setId(2L);

        Edge saved = new Edge();
        saved.setId(100L);
        saved.setDescription("nuevo");
        saved.setStatus("active");
        saved.setProcess(proc);
        saved.setActivitySource(aSrc);
        saved.setActivityDestiny(aDst);
        saved.setFromType("activity");
        saved.setFromId(1L);
        saved.setToType("activity");
        saved.setToId(2L);

        EdgeDTO outDto = dtoLegacy(100L, "nuevo", 10L, 1L, 2L);

        when(processService.findProcessEntity(10L)).thenReturn(proc);
        when(activityService.findActivityEntity(1L)).thenReturn(aSrc);
        when(activityService.findActivityEntity(2L)).thenReturn(aDst);
        when(edgeRepository.save(any(Edge.class))).thenReturn(saved);
        when(modelMapper.map(saved, EdgeDTO.class)).thenReturn(outDto);

        EdgeDTO result = edgeService.createEdge(inDto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getDescription()).isEqualTo("nuevo");
        assertThat(result.getProcessId()).isEqualTo(10L);

        ArgumentCaptor<Edge> captor = ArgumentCaptor.forClass(Edge.class);
        verify(edgeRepository).save(captor.capture());
        Edge savedEdge = captor.getValue();

        assertThat(savedEdge.getActivitySource()).isSameAs(aSrc);
        assertThat(savedEdge.getActivityDestiny()).isSameAs(aDst);
        assertThat(savedEdge.getFromType()).isEqualTo("activity");
        assertThat(savedEdge.getToType()).isEqualTo("activity");
    }

    


    @Test
    void createEdge_typed_activity_gateway_ok() {

            EdgeDTO inDto = dtoTyped(
                    null,
                    "EG",
                    20L,
                    "activity", 5L,
                    "gateway", 9L
            );

            Process proc = new Process(); proc.setId(20L);
            Activity act = new Activity(); act.setId(5L);

            Edge saved = new Edge();
            saved.setId(200L);
            saved.setDescription("EG");
            saved.setStatus("active");
            saved.setProcess(proc);
            saved.setFromType("activity");
            saved.setFromId(5L);
            saved.setToType("gateway");
            saved.setToId(9L);

            EdgeDTO outDto = new EdgeDTO();
            outDto.setId(200L);
            outDto.setDescription("EG");
            outDto.setProcessId(20L);
            outDto.setStatus("active");
            outDto.setFromType("activity");
            outDto.setFromId(5L);
            outDto.setToType("gateway");
            outDto.setToId(9L);

            
            lenient().when(processService.findProcessEntity(20L)).thenReturn(proc);
            lenient().when(activityService.findActivityEntity(5L)).thenReturn(act);

            when(edgeRepository.save(any(Edge.class))).thenReturn(saved);
            when(modelMapper.map(any(Edge.class), eq(EdgeDTO.class))).thenReturn(outDto);

            EdgeDTO result = edgeService.createEdge(inDto);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(200L);
            assertThat(result.getDescription()).isEqualTo("EG");
            assertThat(result.getFromType()).isEqualTo("activity");
            assertThat(result.getToType()).isEqualTo("gateway");
        }




    @Test
    void createEdge_noEndpoints_validationError() {
        EdgeDTO dto = new EdgeDTO();
        dto.setProcessId(10L);
        dto.setDescription("x");

        when(processService.findProcessEntity(10L)).thenReturn(new Process());

        assertThatThrownBy(() -> edgeService.createEdge(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("endpoints");
    }

    @Test
    void createEdge_processNotFound() {
        EdgeDTO dto = dtoLegacy(null, "x", 999L, 1L, 2L);

        when(processService.findProcessEntity(999L))
                .thenThrow(new ResourceNotFoundException("Process", "id", 999L));

        assertThatThrownBy(() -> edgeService.createEdge(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    

    @Test
    void updateEdge_ok() {
        EdgeDTO inDto = dtoLegacy(50L, "editado", 10L, 1L, 2L);

        Process proc = new Process(); proc.setId(10L);
        Activity a1 = new Activity(); a1.setId(1L);
        Activity a2 = new Activity(); a2.setId(2L);

        Edge existing = new Edge(); existing.setId(50L);
        Edge saved = new Edge(); saved.setId(50L);

        when(edgeRepository.findById(50L)).thenReturn(Optional.of(existing));
        when(processService.findProcessEntity(10L)).thenReturn(proc);
        when(activityService.findActivityEntity(1L)).thenReturn(a1);
        when(activityService.findActivityEntity(2L)).thenReturn(a2);
        when(edgeRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, EdgeDTO.class)).thenReturn(inDto);

        EdgeDTO result = edgeService.updateEdge(inDto);

        assertThat(result.getId()).isEqualTo(50L);
    }

    @Test
    void updateEdge_notFound() {
        EdgeDTO dto = dtoLegacy(77L, "x", 10L, 1L, 2L);

        when(edgeRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.updateEdge(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("77");
    }

    

    @Test
    void findEdge_found() {
        Edge entity = new Edge(); entity.setId(3L);
        EdgeDTO outDto = new EdgeDTO(); outDto.setId(3L);

        when(edgeRepository.findById(3L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, EdgeDTO.class)).thenReturn(outDto);

        EdgeDTO result = edgeService.findEdge(3L);

        assertThat(result.getId()).isEqualTo(3L);
    }

    @Test
    void findEdge_notFound() {
        when(edgeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.findEdge(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    

    @Test
    void deleteEdge_ok() {
        Edge existing = new Edge();
        existing.setId(7L);
        existing.setStatus("active");

        when(edgeRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(edgeRepository.save(existing)).thenReturn(existing);

        edgeService.deleteEdge(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");
    }

    @Test
    void deleteEdge_notFound() {
        when(edgeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> edgeService.deleteEdge(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("404");
    }

    

    @Test
    void findEdges_ok() {
        Edge e = new Edge(); e.setId(1L);
        EdgeDTO d = new EdgeDTO(); d.setId(1L);

        when(edgeRepository.findAll()).thenReturn(List.of(e));
        when(modelMapper.map(e, EdgeDTO.class)).thenReturn(d);

        List<EdgeDTO> list = edgeService.findEdges();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(1L);
    }
}

