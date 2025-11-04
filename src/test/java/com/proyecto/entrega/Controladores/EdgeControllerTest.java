package com.proyecto.entrega.Controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.controladores.EdgeController;
import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.service.EdgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EdgeControllerTest {

    private MockMvc mockMvc;
    private EdgeService edgeService;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setup() {
        edgeService = mock(EdgeService.class);
        EdgeController controller = new EdgeController();

        // inyección por reflexión (simple y sin Spring)
        try {
            var f = EdgeController.class.getDeclaredField("edgeService");
            f.setAccessible(true);
            f.set(controller, edgeService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getEdge_returnsOkWithBody_sinIdsWriteOnly() throws Exception {
        EdgeDTO dto = new EdgeDTO();
        dto.setId(1L);
        dto.setDescription("A->B");
        dto.setStatus("active");
        // Estos campos son WRITE_ONLY: pueden estar en el DTO interno, pero no deben serializarse
        dto.setProcessId(10L);
        dto.setActivitySourceId(100L);
        dto.setActivityDestinyId(200L);

        when(edgeService.findEdge(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/edge/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("A->B")))
                .andExpect(jsonPath("$.status", is("active")))
                // No deben existir por ser WRITE_ONLY
                .andExpect(jsonPath("$.processId").doesNotExist())
                .andExpect(jsonPath("$.activitySourceId").doesNotExist())
                .andExpect(jsonPath("$.activityDestinyId").doesNotExist());
    }

    @Test
    void getEdges_returnsList_sinIdsWriteOnly() throws Exception {
        EdgeDTO a = new EdgeDTO();
        a.setId(1L); a.setDescription("A->B"); a.setStatus("active");
        a.setProcessId(10L); a.setActivitySourceId(100L); a.setActivityDestinyId(200L);

        EdgeDTO b = new EdgeDTO();
        b.setId(2L); b.setDescription("B->C"); b.setStatus("inactive");
        b.setProcessId(20L); b.setActivitySourceId(200L); b.setActivityDestinyId(300L);

        when(edgeService.findEdges()).thenReturn(List.of(a, b));

        mockMvc.perform(get("/api/edge"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].id", is(1)))
                .andExpect(jsonPath("[0].description", is("A->B")))
                .andExpect(jsonPath("[0].status", is("active")))
                .andExpect(jsonPath("[0].processId").doesNotExist())
                .andExpect(jsonPath("[0].activitySourceId").doesNotExist())
                .andExpect(jsonPath("[0].activityDestinyId").doesNotExist())
                .andExpect(jsonPath("[1].id", is(2)))
                .andExpect(jsonPath("[1].description", is("B->C")))
                .andExpect(jsonPath("[1].status", is("inactive")))
                .andExpect(jsonPath("[1].processId").doesNotExist())
                .andExpect(jsonPath("[1].activitySourceId").doesNotExist())
                .andExpect(jsonPath("[1].activityDestinyId").doesNotExist());
    }

    @Test
    void createEdge_returnsOk_andCallsService_enviandoIdsComoLong() throws Exception {
        EdgeDTO payload = new EdgeDTO();
        payload.setDescription("Nueva");
        payload.setStatus("active");
        payload.setProcessId(99L);
        payload.setActivitySourceId(1000L);
        payload.setActivityDestinyId(2000L);

        EdgeDTO returned = new EdgeDTO();
        returned.setId(10L);
        returned.setDescription("Nueva");
        returned.setStatus("active");
        returned.setProcessId(99L);
        returned.setActivitySourceId(1000L);
        returned.setActivityDestinyId(2000L);

        when(edgeService.createEdge(any(EdgeDTO.class))).thenReturn(returned);

        mockMvc.perform(post("/api/edge")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        ArgumentCaptor<EdgeDTO> captor = ArgumentCaptor.forClass(EdgeDTO.class);
        verify(edgeService).createEdge(captor.capture());
        EdgeDTO sent = captor.getValue();
        // Aseguramos que viajan como Longs
        org.assertj.core.api.Assertions.assertThat(sent.getProcessId()).isEqualTo(99L);
        org.assertj.core.api.Assertions.assertThat(sent.getActivitySourceId()).isEqualTo(1000L);
        org.assertj.core.api.Assertions.assertThat(sent.getActivityDestinyId()).isEqualTo(2000L);
    }

    @Test
    void updateEdge_returnsOk_andCallsService_enviandoIdsComoLong() throws Exception {
        EdgeDTO payload = new EdgeDTO();
        payload.setId(5L);
        payload.setDescription("Editada");
        payload.setStatus("active");
        payload.setProcessId(77L);
        payload.setActivitySourceId(111L);
        payload.setActivityDestinyId(222L);

        when(edgeService.updateEdge(any(EdgeDTO.class))).thenReturn(payload);

        mockMvc.perform(put("/api/edge")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        ArgumentCaptor<EdgeDTO> captor = ArgumentCaptor.forClass(EdgeDTO.class);
        verify(edgeService).updateEdge(captor.capture());
        EdgeDTO sent = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(sent.getId()).isEqualTo(5L);
        org.assertj.core.api.Assertions.assertThat(sent.getProcessId()).isEqualTo(77L);
        org.assertj.core.api.Assertions.assertThat(sent.getActivitySourceId()).isEqualTo(111L);
        org.assertj.core.api.Assertions.assertThat(sent.getActivityDestinyId()).isEqualTo(222L);
    }

    @Test
    void deleteEdge_returnsOk_andCallsService() throws Exception {
        mockMvc.perform(delete("/api/edge/{id}", 9L))
                .andExpect(status().isOk());

        verify(edgeService).deleteEdge(9L);
    }
}
