package com.proyecto.entrega.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    private static final String BASE = "/api/company";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean CompanyService companyService; // <- reemplaza @MockBean

    @Test
    void getCompanyById_ok() throws Exception {
        CompanyDTO dto = new CompanyDTO(1L, 900123456L, "ACME", "contacto@acme.com");
        when(companyService.findCompany(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                // Propiedad "NIT" en mayúscula (según tu DTO)
                .andExpect(jsonPath("$.NIT", is(900123456)))
                .andExpect(jsonPath("$.nombre", is("ACME")))
                .andExpect(jsonPath("$.correoContacto", is("contacto@acme.com")));

        verify(companyService).findCompany(1L);
    }

    @Test
    void listCompanies_ok() throws Exception {
        List<CompanyDTO> list = List.of(
                new CompanyDTO(1L, 1L, "A", "a@a.com"),
                new CompanyDTO(2L, 2L, "B", "b@b.com")
        );
        when(companyService.findCompany()).thenReturn(list);

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(companyService).findCompany();
    }

    @Test
    void createCompany_ok() throws Exception {
        CompanyDTO payload = new CompanyDTO(null, 999L, "Nueva", "nueva@co.com");

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(companyService).createCompany(any(CompanyDTO.class));
    }

    @Test
    void updateCompany_ok() throws Exception {
        CompanyDTO payload = new CompanyDTO(10L, 888L, "Editada", "editada@co.com");

        mockMvc.perform(put(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(companyService).updateCompany(any(CompanyDTO.class));
    }

    @Test
    void deleteCompany_ok() throws Exception {
        mockMvc.perform(delete(BASE + "/{id}", 7L))
                .andExpect(status().isOk());

        verify(companyService).deleteCompany(7L);
    }
}
