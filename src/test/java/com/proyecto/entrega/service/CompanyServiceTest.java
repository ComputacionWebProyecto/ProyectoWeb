package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    CompanyRepository companyRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CompanyService companyService;

    @Test
    void createCompany_ok() {
        CompanyDTO inDto = new CompanyDTO(null, 900123456L, "Acme", "contacto@acme.com");
        Company entityBefore = new Company();
        entityBefore.setId(null);
        entityBefore.setNIT(900123456L);
        entityBefore.setName("Acme");
        entityBefore.setCorreoContacto("contacto@acme.com");

        Company entitySaved = new Company();
        entitySaved.setId(1L);
        entitySaved.setNIT(900123456L);
        entitySaved.setName("Acme");
        entitySaved.setCorreoContacto("contacto@acme.com");

        CompanyDTO outDto = new CompanyDTO(1L, 900123456L, "Acme", "contacto@acme.com");

        when(modelMapper.map(inDto, Company.class)).thenReturn(entityBefore);
        when(companyRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, CompanyDTO.class)).thenReturn(outDto);

        CompanyDTO result = companyService.createCompany(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNIT()).isEqualTo(900123456L);
        assertThat(result.getNombre()).isEqualTo("Acme");
        verify(companyRepository).save(entityBefore);
    }

    @Test
    void updateCompany_ok() {
        CompanyDTO inDto = new CompanyDTO(5L, 800111222L, "Editada", "edit@co.com");

        Company entity = new Company();
        entity.setId(5L);
        entity.setNIT(800111222L);
        entity.setName("Editada");
        entity.setCorreoContacto("edit@co.com");

        when(modelMapper.map(inDto, Company.class)).thenReturn(entity);
        when(companyRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, CompanyDTO.class)).thenReturn(inDto);

        CompanyDTO result = companyService.updateCompany(inDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getNombre()).isEqualTo("Editada");
        verify(companyRepository).save(entity);
    }

    @Test
    void findCompany_found() {
        Company entity = new Company();
        entity.setId(2L);
        entity.setNIT(777L);
        entity.setName("Comp2");
        entity.setCorreoContacto("c2@co.com");

        CompanyDTO dto = new CompanyDTO(2L, 777L, "Comp2", "c2@co.com");

        when(companyRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, CompanyDTO.class)).thenReturn(dto);

        CompanyDTO result = companyService.findCompany(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getNombre()).isEqualTo("Comp2");
    }

    @Test
    void findCompany_notFound_throwsEntityNotFound() {
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.findCompany(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(companyRepository).findById(999L);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void deleteCompany_ok() {
        companyService.deleteCompany(7L);
        verify(companyRepository).deleteById(7L);
    }

    @Test
    void findCompany_list_ok() {
        Company a = new Company();
        a.setId(1L); a.setNIT(1L); a.setName("A"); a.setCorreoContacto("a@co.com");

        when(companyRepository.findAll()).thenReturn(List.of(a));

        CompanyDTO dto = new CompanyDTO(1L, 1L, "A", "a@co.com");
        when(modelMapper.map(a, CompanyDTO.class)).thenReturn(dto);

        List<CompanyDTO> result = companyService.findCompany();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("A");
        verify(companyRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Company.class), eq(CompanyDTO.class));
    }
}
