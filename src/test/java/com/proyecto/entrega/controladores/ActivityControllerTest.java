package com.proyecto.entrega.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.service.ActivityService;
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

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    private static final String BASE = "/api/activity";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean ActivityService activityService;

    @Test
    void getById_ok() throws Exception {
        ActivityDTO dto = new ActivityDTO(1L, "A1", 10.0, 20.0, "desc", 100.0, 50.0, "active");
        when(activityService.findActivity(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("A1")))
                .andExpect(jsonPath("$.status", is("active")));

        verify(activityService).findActivity(1L);
    }

    @Test
    void list_ok() throws Exception {
        List<ActivityDTO> list = List.of(
                new ActivityDTO(1L, "A1", 10.0, 20.0, "d1", 100.0, 50.0, "active"),
                new ActivityDTO(2L, "A2", 30.0, 40.0, "d2", 120.0, 60.0, "active")
        );
        when(activityService.findActivities()).thenReturn(list);

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(activityService).findActivities();
    }

    @Test
    void create_ok() throws Exception {
        ActivityDTO payload = new ActivityDTO(null, "Nueva", 1.0, 2.0, "desc", 10.0, 5.0, "active");

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(activityService).createActivity(any(ActivityDTO.class));
    }

    @Test
    void update_ok() throws Exception {
        ActivityDTO payload = new ActivityDTO(5L, "Editada", 1.0, 2.0, "desc", 10.0, 5.0, "active");

        mockMvc.perform(put(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(activityService).updateActivity(any(ActivityDTO.class));
    }

    @Test
    void delete_ok() throws Exception {
        mockMvc.perform(delete(BASE + "/{id}", 7L))
                .andExpect(status().isOk());

        verify(activityService).deleteActivity(7L);
    }
}
