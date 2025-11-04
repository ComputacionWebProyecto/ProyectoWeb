package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock CompanyRepository companyRepository;
    @Mock ModelMapper modelMapper;

    @InjectMocks CompanyService companyService;

    // -------- helpers
    private Company company(Long id, Long nit, String name, String correo, String status) {
        Company c = new Company();
        c.setId(id);
        c.setNit(nit);
        c.setName(name);
        c.setCorreoContacto(correo);
        c.setStatus(status);
        return c;
    }

    private CompanyDTO dto(Long id, Long nit, String name, String correo, String status) {
        CompanyDTO d = new CompanyDTO();
        d.setId(id);
        d.setNit(nit);
        d.setName(name);
        d.setCorreoContacto(correo);
        d.setStatus(status);
        return d;
    }

    // =================== CREATE ===================

    @Test
    void createCompany_ok() {
        CompanyDTO inDto = dto(null, 900123456L, "Acme", "contacto@acme.com", null);
        Company toPersist = company(null, 900123456L, "Acme", "contacto@acme.com", "active");
        Company persisted = company(1L, 900123456L, "Acme", "contacto@acme.com", "active");
        CompanyDTO outDto = dto(1L, 900123456L, "Acme", "contacto@acme.com", "active");

        when(companyRepository.existsByName("Acme")).thenReturn(false);
        when(companyRepository.existsByCorreoContacto("contacto@acme.com")).thenReturn(false);
        when(modelMapper.map(inDto, Company.class)).thenReturn(toPersist);
        when(companyRepository.save(toPersist)).thenReturn(persisted);
        when(modelMapper.map(persisted, CompanyDTO.class)).thenReturn(outDto);

        CompanyDTO result = companyService.createCompany(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("active");
        verify(companyRepository).save(toPersist);
    }

    @Test
    void createCompany_nombreDuplicado_badRequest() {
        CompanyDTO inDto = dto(null, 1L, "Repetida", "c@co.com", null);
        when(companyRepository.existsByName("Repetida")).thenReturn(true);

        ResponseStatusException ex = catchThrowableOfType(
                () -> companyService.createCompany(inDto),
                ResponseStatusException.class
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getReason()).contains("Nombre de compañía ya existe");

        verify(companyRepository).existsByName("Repetida");
        verifyNoMoreInteractions(companyRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void createCompany_correoDuplicado_badRequest() {
        CompanyDTO inDto = dto(null, 1L, "OkName", "dup@co.com", null);
        when(companyRepository.existsByName("OkName")).thenReturn(false);
        when(companyRepository.existsByCorreoContacto("dup@co.com")).thenReturn(true);

        ResponseStatusException ex = catchThrowableOfType(
                () -> companyService.createCompany(inDto),
                ResponseStatusException.class
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getReason()).contains("Correo de compañía ya existe");

        verify(companyRepository).existsByName("OkName");
        verify(companyRepository).existsByCorreoContacto("dup@co.com");
        verifyNoMoreInteractions(companyRepository);
        verifyNoInteractions(modelMapper);
    }

    // =================== UPDATE ===================

    @Test
    void updateCompany_ok() {
        CompanyDTO inDto = dto(5L, 800111222L, "Editada", "edit@co.com", "active");

        Company existing = company(5L, 999L, "Vieja", "old@co.com", "active");
        Company saved = company(5L, 800111222L, "Editada", "edit@co.com", "active");

        when(companyRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(companyRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, CompanyDTO.class)).thenReturn(inDto);

        CompanyDTO result = companyService.updateCompany(inDto);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(existing.getNit()).isEqualTo(800111222L);
        assertThat(existing.getName()).isEqualTo("Editada");
        assertThat(existing.getCorreoContacto()).isEqualTo("edit@co.com");
        verify(companyRepository).save(existing);
    }

    @Test
    void updateCompany_idNulo_illegalArgument() {
        CompanyDTO inDto = dto(null, 1L, "X", "x@co.com", "active");

        assertThatThrownBy(() -> companyService.updateCompany(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id is required for update");

        verifyNoInteractions(companyRepository, modelMapper);
    }

    @Test
    void updateCompany_noExiste_entityNotFound() {
        CompanyDTO inDto = dto(77L, 1L, "X", "x@co.com", "active");
        when(companyRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.updateCompany(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Company 77");

        verify(companyRepository).findById(77L);
        verifyNoMoreInteractions(companyRepository);
        verifyNoInteractions(modelMapper);
    }

    // =================== FIND (DTO & ENTITY) ===================

    @Test
    void findCompany_found() {
        Company entity = company(2L, 777L, "Comp2", "c2@co.com", "active");
        CompanyDTO dto = dto(2L, 777L, "Comp2", "c2@co.com", "active");

        when(companyRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, CompanyDTO.class)).thenReturn(dto);

        CompanyDTO result = companyService.findCompany(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo("active");
    }

    @Test
    void findCompany_notFound() {
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.findCompany(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findCompanyEntity_found() {
        Company entity = company(3L, 1L, "E", "e@co.com", "active");
        when(companyRepository.findById(3L)).thenReturn(Optional.of(entity));

        Company result = companyService.findCompanyEntity(3L);

        assertThat(result.getId()).isEqualTo(3L);
    }

    @Test
    void findCompanyEntity_notFound() {
        when(companyRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.findCompanyEntity(404L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("404");
    }

    // =================== DELETE (soft) ===================

    @Test
    void deleteCompany_ok_softDelete() {
        Company entity = company(7L, 111L, "Empresa", "correo@test.com", "active");
        when(companyRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(companyRepository.save(entity)).thenReturn(entity);

        companyService.deleteCompany(7L);

        assertThat(entity.getStatus()).isEqualTo("inactive");
        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("inactive");
    }

    @Test
    void deleteCompany_notFound() {
        when(companyRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.deleteCompany(123L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("123");

        verify(companyRepository).findById(123L);
        verifyNoMoreInteractions(companyRepository);
    }

    // =================== LIST ===================

    @Test
    void findCompanies_ok() {
        Company a = company(1L, 1L, "A", "a@co.com", "active");
        Company b = company(2L, 2L, "B", "b@co.com", "active");
        when(companyRepository.findAll()).thenReturn(List.of(a, b));

        CompanyDTO da = dto(1L, 1L, "A", "a@co.com", "active");
        CompanyDTO db = dto(2L, 2L, "B", "b@co.com", "active");
        when(modelMapper.map(a, CompanyDTO.class)).thenReturn(da);
        when(modelMapper.map(b, CompanyDTO.class)).thenReturn(db);

        List<CompanyDTO> result = companyService.findCompanies();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(1).getName()).isEqualTo("B");
        verify(companyRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Company.class), eq(CompanyDTO.class));
    }
}
