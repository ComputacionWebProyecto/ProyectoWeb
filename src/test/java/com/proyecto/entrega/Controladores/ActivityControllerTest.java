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
        // Inyección manual del mock
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
    void getActivity_returnsOkWithBody_sinProcessNiRoleIdsEnJson() throws Exception {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(1L);
        dto.setName("Task A");
        dto.setX(10.0);
        dto.setY(20.0);
        dto.setDescription("desc");
        dto.setWidth(100.0);
        dto.setHeight(50.0);
        dto.setStatus("active");
        // Aunque el service devuelva processId/roleId internamente, WRITE_ONLY => no deben serializarse
        dto.setProcessId(5L);
        dto.setRoleId(9L);

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
                .andExpect(jsonPath("$.status", is("active")))
                // No deben existir por ser WRITE_ONLY
                .andExpect(jsonPath("$.processId").doesNotExist())
                .andExpect(jsonPath("$.roleId").doesNotExist());
    }

    @Test
    void getActivities_returnsList_sinProcessNiRoleIdsEnJson() throws Exception {
        ActivityDTO a = new ActivityDTO();
        a.setId(1L); a.setName("A"); a.setX(1.0); a.setY(2.0);
        a.setDescription("d1"); a.setWidth(3.0); a.setHeight(4.0); a.setStatus("active");
        a.setProcessId(10L); a.setRoleId(100L); // WRITE_ONLY -> no deben aparecer

        ActivityDTO b = new ActivityDTO();
        b.setId(2L); b.setName("B"); b.setX(5.0); b.setY(6.0);
        b.setDescription("d2"); b.setWidth(7.0); b.setHeight(8.0); b.setStatus("inactive");
        b.setProcessId(20L); b.setRoleId(200L);

        when(activityService.findActivities()).thenReturn(List.of(a, b));

        mockMvc.perform(get("/api/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("A")))
                .andExpect(jsonPath("$[0].status", is("active")))
                .andExpect(jsonPath("$[0].processId").doesNotExist())
                .andExpect(jsonPath("$[0].roleId").doesNotExist())
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("B")))
                .andExpect(jsonPath("$[1].status", is("inactive")))
                .andExpect(jsonPath("$[1].processId").doesNotExist())
                .andExpect(jsonPath("$[1].roleId").doesNotExist());
    }

    @Test
    void createActivity_returnsOk_andCallsService_enviandoIdsComoLong() throws Exception {
        ActivityDTO payload = new ActivityDTO();
        payload.setName("Nueva");
        payload.setX(1.0);
        payload.setY(2.0);
        payload.setDescription("d");
        payload.setWidth(3.0);
        payload.setHeight(4.0);
        payload.setStatus("active");
        payload.setProcessId(99L);
        payload.setRoleId(9L);

        ActivityDTO returned = new ActivityDTO();
        returned.setId(10L);
        returned.setName("Nueva");
        returned.setX(1.0);
        returned.setY(2.0);
        returned.setDescription("d");
        returned.setWidth(3.0);
        returned.setHeight(4.0);
        returned.setStatus("active");
        returned.setProcessId(99L);
        returned.setRoleId(9L);

        when(activityService.createActivity(any(ActivityDTO.class))).thenReturn(returned);

        mockMvc.perform(post("/api/activity")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        ArgumentCaptor<ActivityDTO> captor = ArgumentCaptor.forClass(ActivityDTO.class);
        verify(activityService).createActivity(captor.capture());
        ActivityDTO sent = captor.getValue();
        // WRITE_ONLY -> solo importa que se le pase al servicio correctamente
        org.assertj.core.api.Assertions.assertThat(sent.getProcessId()).isEqualTo(99L);
        org.assertj.core.api.Assertions.assertThat(sent.getRoleId()).isEqualTo(9L);
    }

    @Test
    void updateActivity_returnsOk_andCallsService_enviandoIdsComoLong() throws Exception {
        ActivityDTO payload = new ActivityDTO();
        payload.setId(10L);
        payload.setName("Editada");
        payload.setX(1.0);
        payload.setY(2.0);
        payload.setDescription("d");
        payload.setWidth(3.0);
        payload.setHeight(4.0);
        payload.setStatus("active");
        payload.setProcessId(77L);
        payload.setRoleId(7L);

        when(activityService.updateActivity(any(ActivityDTO.class))).thenReturn(payload);

        mockMvc.perform(put("/api/activity")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        ArgumentCaptor<ActivityDTO> captor = ArgumentCaptor.forClass(ActivityDTO.class);
        verify(activityService).updateActivity(captor.capture());
        org.assertj.core.api.Assertions.assertThat(captor.getValue().getProcessId()).isEqualTo(77L);
        org.assertj.core.api.Assertions.assertThat(captor.getValue().getRoleId()).isEqualTo(7L);
    }

    @Test
    void deleteActivity_returnsOk_andCallsService() throws Exception {
        mockMvc.perform(delete("/api/activity/{id}", 5L))
                .andExpect(status().isOk());

        verify(activityService).deleteActivity(5L);
    }
}
