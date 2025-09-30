package com.proyecto.entrega.Controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.controladores.CompanyController;
import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.service.CompanyService;
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

class CompanyControllerTest {

    private MockMvc mockMvc;
    private CompanyService companyService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        companyService = Mockito.mock(CompanyService.class);

        CompanyController controller = new CompanyController();
        try {
            Field f = CompanyController.class.getDeclaredField("companyService");
            f.setAccessible(true);
            f.set(controller, companyService);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo inyectar CompanyService en CompanyController", e);
        }

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getCompany_byId_returnsOkWithBody() throws Exception {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(1L);
        dto.setNit(900123456L);
        dto.setName("Acme Inc.");
        dto.setCorreoContacto("contacto@acme.com");

        Mockito.when(companyService.findCompany(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.NIT").value(900123456))
                .andExpect(jsonPath("$.name").value("Acme Inc."))
                .andExpect(jsonPath("$.correoContacto").value("contacto@acme.com"));
    }

    @Test
    void getCompany_list_returnsOkWithArray() throws Exception {
        CompanyDTO a = new CompanyDTO(1L, 123L, "A", "a@co.com");
        CompanyDTO b = new CompanyDTO(2L, 456L, "B", "b@co.com");

        Mockito.when(companyService.findCompany()).thenReturn(List.of(a, b));

        mockMvc.perform(get("/api/company"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].NIT").value(123))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[0].correoContacto").value("a@co.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].NIT").value(456))
                .andExpect(jsonPath("$[1].name").value("B"))
                .andExpect(jsonPath("$[1].correoContacto").value("b@co.com"));
    }

    @Test
    void createCompany_returnsOk_andCallsService() throws Exception {
        CompanyDTO payload = new CompanyDTO();
        payload.setNit(900777888L);
        payload.setName("Nueva Co");
        payload.setCorreoContacto("hola@nueva.co");

        mockMvc.perform(post("/api/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(companyService).createCompany(any(CompanyDTO.class));
    }

    @Test
    void updateCompany_returnsOk_andCallsService() throws Exception {
        CompanyDTO payload = new CompanyDTO();
        payload.setId(10L);
        payload.setNit(999L);
        payload.setName("Editada");
        payload.setCorreoContacto("editada@co.com");

        mockMvc.perform(put("/api/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(companyService).updateCompany(any(CompanyDTO.class));
    }

    @Test
    void deleteCompany_returnsOk_andCallsService() throws Exception {
        mockMvc.perform(delete("/api/company/7"))
                .andExpect(status().isOk());

        verify(companyService).deleteCompany(7L);
    }
}
