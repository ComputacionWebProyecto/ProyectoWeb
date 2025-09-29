package com.proyecto.entrega.Controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.controladores.EdgeController;
import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.service.EdgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EdgeControllerTest {

    private MockMvc mockMvc;
    private EdgeService edgeService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        edgeService = Mockito.mock(EdgeService.class);

        // Controller real
        EdgeController controller = new EdgeController();

        // Inyectar el service (@Autowired por campo) vía reflexión
        try {
            Field f = EdgeController.class.getDeclaredField("edgeService");
            f.setAccessible(true);
            f.set(controller, edgeService);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo inyectar EdgeService en EdgeController", e);
        }

        // Construir MockMvc solo con este controller
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getEdge_byId_returnsOkWithBody() throws Exception {
        EdgeDTO dto = new EdgeDTO(1L, "flujo principal", "active");
        Mockito.when(edgeService.findEdge(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/edge/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.description").value("flujo principal"))
               .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    void getEdges_list_returnsOkArray() throws Exception {
        List<EdgeDTO> list = List.of(
                new EdgeDTO(1L, "camino A→B", "active"),
                new EdgeDTO(2L, "camino B→C", "inactive")
        );
        Mockito.when(edgeService.findEdges()).thenReturn(list);

        mockMvc.perform(get("/api/edge"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].description").value("camino A→B"))
               .andExpect(jsonPath("$[0].status").value("active"))
               .andExpect(jsonPath("$[1].id").value(2))
               .andExpect(jsonPath("$[1].description").value("camino B→C"))
               .andExpect(jsonPath("$[1].status").value("inactive"));
    }

    @Test
    void createEdge_returnsOk_andCallsService() throws Exception {
        EdgeDTO payload = new EdgeDTO(null, "nuevo enlace", "active");

        mockMvc.perform(post("/api/edge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
               .andExpect(status().isOk());

        verify(edgeService).createEdge(any(EdgeDTO.class));
    }

    @Test
    void updateEdge_returnsOk_andCallsService() throws Exception {
        EdgeDTO payload = new EdgeDTO(10L, "enlace editado", "active");

        mockMvc.perform(put("/api/edge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
               .andExpect(status().isOk());

        verify(edgeService).updateEdge(any(EdgeDTO.class));
    }

    @Test
    void deleteEdge_returnsOk_andCallsService() throws Exception {
        mockMvc.perform(delete("/api/edge/7"))
               .andExpect(status().isOk());

        verify(edgeService).deleteEdge(7L);
    }
}
