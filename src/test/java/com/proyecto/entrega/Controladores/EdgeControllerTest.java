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
    void getEdge_returnsOkWithBody() throws Exception {
        EdgeDTO dto = new EdgeDTO(1L, "A->B", 10L, 100L, 200L);
        when(edgeService.findEdge(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/edge/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("A->B")))
                .andExpect(jsonPath("$.processId", is(10)))
                .andExpect(jsonPath("$.activitySourceId", is(100)))
                .andExpect(jsonPath("$.activityDestinyId", is(200)));
    }

    @Test
    void getEdges_returnsList() throws Exception {
        var dto1 = new EdgeDTO(1L, "A->B", 10L, 100L, 200L);
        var dto2 = new EdgeDTO(2L, "B->C", 20L, 200L, 300L);
        when(edgeService.findEdges()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/edge"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].id", is(1)))
                .andExpect(jsonPath("[0].description", is("A->B")))
                .andExpect(jsonPath("[0].processId", is(10)))
                .andExpect(jsonPath("[0].activitySourceId", is(100)))
                .andExpect(jsonPath("[0].activityDestinyId", is(200)))
                .andExpect(jsonPath("[1].id", is(2)))
                .andExpect(jsonPath("[1].description", is("B->C")))
                .andExpect(jsonPath("[1].processId", is(20)))
                .andExpect(jsonPath("[1].activitySourceId", is(200)))
                .andExpect(jsonPath("[1].activityDestinyId", is(300)));
    }

    @Test
    void createEdge_returnsOk_andCallsService() throws Exception {
        EdgeDTO payload = new EdgeDTO(null, "Nueva", 99L, 1000L, 2000L);
        // el controller devuelve void; el service devuelve DTO (lo mockeamos igual)
        when(edgeService.createEdge(any(EdgeDTO.class)))
                .thenReturn(new EdgeDTO(10L, "Nueva", 99L, 1000L, 2000L));

        mockMvc.perform(post("/api/edge")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        ArgumentCaptor<EdgeDTO> captor = ArgumentCaptor.forClass(EdgeDTO.class);
        verify(edgeService).createEdge(captor.capture());
        EdgeDTO sent = captor.getValue();
        // aseguramos que viajan números (no strings)
        org.assertj.core.api.Assertions.assertThat(sent.getProcessId()).isEqualTo(99L);
        org.assertj.core.api.Assertions.assertThat(sent.getActivitySourceId()).isEqualTo(1000L);
        org.assertj.core.api.Assertions.assertThat(sent.getActivityDestinyId()).isEqualTo(2000L);
    }

    @Test
    void updateEdge_returnsOk_andCallsService() throws Exception {
        EdgeDTO payload = new EdgeDTO(5L, "Editada", 77L, 111L, 222L);
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
