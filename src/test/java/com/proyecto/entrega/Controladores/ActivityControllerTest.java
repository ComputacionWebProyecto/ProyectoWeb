package com.proyecto.entrega.Controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.controladores.ActivityController;
import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ActivityControllerTest {

    private MockMvc mockMvc;
    private ActivityService activityService;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setup() {
        activityService = mock(ActivityService.class);
        ActivityController controller = new ActivityController();
        // inyectamos el mock “a la brava”
        try {
            var f = ActivityController.class.getDeclaredField("activityService");
            f.setAccessible(true);
            f.set(controller, activityService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getActivity_returnsOkWithBody() throws Exception {
        ActivityDTO dto = new ActivityDTO(
                1L, "Task A", 10.0, 20.0, "desc", 100.0, 50.0, 5L
        );
        when(activityService.findActivity(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/activity/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Task A")))
                .andExpect(jsonPath("$.x", is(10.0)))
                .andExpect(jsonPath("$.y", is(20.0)))
                .andExpect(jsonPath("$.description", is("desc")))
                .andExpect(jsonPath("$.width", is(100.0)))
                .andExpect(jsonPath("$.height", is(50.0)))
                .andExpect(jsonPath("$.processId", is(5))); // NUMÉRICO, no "5"
    }

    @Test
    void getActivities_returnsList() throws Exception {
        var dto1 = new ActivityDTO(1L, "A", 1.0, 2.0, "d1", 3.0, 4.0, 10L);
        var dto2 = new ActivityDTO(2L, "B", 5.0, 6.0, "d2", 7.0, 8.0, 20L);
        when(activityService.findActivities()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("A")))
                .andExpect(jsonPath("$[0].processId", is(10)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("B")))
                .andExpect(jsonPath("$[1].processId", is(20)));
    }

    @Test
    void createActivity_returnsOk_andCallsService() throws Exception {
        ActivityDTO payload = new ActivityDTO(
                null, "Nueva", 1.0, 2.0, "d", 3.0, 4.0, 99L
        );
        // el controller no devuelve body (void), solo verificamos la llamadaa
        when(activityService.createActivity(any(ActivityDTO.class)))
                .thenReturn(new ActivityDTO(10L, "Nueva", 1.0, 2.0, "d", 3.0, 4.0, 99L));

        mockMvc.perform(post("/api/activity")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        ArgumentCaptor<ActivityDTO> captor = ArgumentCaptor.forClass(ActivityDTO.class);
        verify(activityService).createActivity(captor.capture());
        ActivityDTO sent = captor.getValue();
        // Aseguramos que processId llegó como Long (no como String)
        org.assertj.core.api.Assertions.assertThat(sent.getProcessId()).isEqualTo(99L);
    }

    @Test
    void updateActivity_returnsOk_andCallsService() throws Exception {
        ActivityDTO payload = new ActivityDTO(
                10L, "Editada", 1.0, 2.0, "d", 3.0, 4.0, 77L
        );
        when(activityService.updateActivity(any(ActivityDTO.class)))
                .thenReturn(payload);

        mockMvc.perform(put("/api/activity")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        ArgumentCaptor<ActivityDTO> captor = ArgumentCaptor.forClass(ActivityDTO.class);
        verify(activityService).updateActivity(captor.capture());
        org.assertj.core.api.Assertions.assertThat(captor.getValue().getProcessId()).isEqualTo(77L);
    }

    @Test
    void deleteActivity_returnsOk_andCallsService() throws Exception {
        mockMvc.perform(delete("/api/activity/{id}", 5L))
                .andExpect(status().isOk());

        verify(activityService).deleteActivity(5L);
    }
}
