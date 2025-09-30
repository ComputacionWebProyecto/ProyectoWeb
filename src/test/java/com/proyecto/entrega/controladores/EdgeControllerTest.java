package com.proyecto.entrega.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.service.EdgeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EdgeController.class)
class EdgeControllerTest {

    private static final String BASE = "/api/edge"; // ajusta a plural si tu controller lo usa

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean EdgeService edgeService;

    @Test
    void getById_ok() throws Exception {
        EdgeDTO dto = new EdgeDTO(1L, "conecta A->B", "active");
        when(edgeService.findEdge(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("conecta A->B")))
                .andExpect(jsonPath("$.status", is("active")));

        verify(edgeService).findEdge(1L);
    }

    @Test
    void list_ok() throws Exception {
        List<EdgeDTO> list = List.of(
                new EdgeDTO(1L, "A->G", "active"),
                new EdgeDTO(2L, "G->B", "active")
        );
        when(edgeService.findEdges()).thenReturn(list);

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(edgeService).findEdges();
    }

    @Test
    void create_ok() throws Exception {
        EdgeDTO payload = new EdgeDTO(null, "A->B", "active");

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(edgeService).createEdge(any(EdgeDTO.class));
    }

    @Test
    void update_ok() throws Exception {
        EdgeDTO payload = new EdgeDTO(5L, "A->C", "inactive");

        mockMvc.perform(put(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(edgeService).updateEdge(any(EdgeDTO.class));
    }

    @Test
    void delete_ok() throws Exception {
        mockMvc.perform(delete(BASE + "/{id}", 9L))
                .andExpect(status().isOk());

        verify(edgeService).deleteEdge(9L);
    }
}
