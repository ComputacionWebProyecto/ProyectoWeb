package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.CompanyRepository;
import com.proyecto.entrega.repository.ProcessRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessServiceTest {

    @Mock ProcessRepository processRepository;
    @Mock CompanyRepository companyRepository;
    @Mock ModelMapper modelMapper;

    @InjectMocks ProcessService processService;

    @Test
    void createProcess_ok() {
        // DTO ahora requiere companyId
        ProcessDTO inDto = new ProcessDTO(null, "Proc A", "desc A", 77L);

        Company company = new Company();
        company.setId(77L);

        Process entityBefore = new Process(null, "Proc A", "desc A", "active", company, null, null);
        Process entitySaved  = new Process(1L,   "Proc A", "desc A", "active", company, null, null);

        ProcessDTO outDto    = new ProcessDTO(1L, "Proc A", "desc A", 77L);

        when(companyRepository.findById(77L)).thenReturn(Optional.of(company));
        when(modelMapper.map(inDto, Process.class)).thenReturn(entityBefore);
        when(processRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, ProcessDTO.class)).thenReturn(outDto);

        ProcessDTO result = processService.createProcess(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Proc A");
        assertThat(result.getCompanyId()).isEqualTo(77L);

        verify(companyRepository).findById(77L);
        verify(processRepository).save(entityBefore);
    }

    @Test
    void updateProcess_ok() {
            ProcessDTO inDto = new ProcessDTO(10L, "Proc Edit", "d", 77L);

        Company company = new Company();
        company.setId(77L);

        Process existing = new Process(10L, "Old", "old", "active", company, null, null);
        // Simulamos el estado esperado luego del update
        Process saved    = new Process(10L, "Proc Edit", "d", "active", company, null, null);

        ProcessDTO outDto = new ProcessDTO(10L, "Proc Edit", "d", 77L);

        when(processRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(companyRepository.findById(77L)).thenReturn(Optional.of(company));

        // Si el service actualiza campos manualmente, basta con stubbear el save y el map final:
        when(processRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, ProcessDTO.class)).thenReturn(outDto);

        ProcessDTO result = processService.updateProcess(inDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Proc Edit");
        assertThat(result.getCompanyId()).isEqualTo(77L);

        // Verificaciones mínimas
        verify(processRepository).findById(10L);
        verify(companyRepository).findById(77L);
        verify(processRepository).save(existing);
        verify(modelMapper).map(saved, ProcessDTO.class);

        // (Opcional) Asegura que el entity en memoria quedó actualizado
        assertThat(existing.getName()).isEqualTo("Proc Edit");
        assertThat(existing.getDescription()).isEqualTo("d");
        assertThat(existing.getCompany()).isSameAs(company);
    }

    @Test
    void findProcess_found() {
        Company company = new Company();
        company.setId(50L);

        Process entity = new Process(2L, "Proc B", "desc B", "active", company, null, null);
        ProcessDTO dto = new ProcessDTO(2L, "Proc B", "desc B", 50L);

        when(processRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ProcessDTO.class)).thenReturn(dto);

        ProcessDTO result = processService.findProcess(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Proc B");
        assertThat(result.getCompanyId()).isEqualTo(50L);
    }

    @Test
    void findProcess_notFound_throwsEntityNotFound() {
        when(processRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processService.findProcess(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(processRepository).findById(999L);
        verifyNoMoreInteractions(processRepository);
    }

    @Test
    void deleteProcess_ok() {
        Company company = new Company();
        company.setId(77L);
        Process existing = new Process(7L, "X", "Y", "active", company, null, null);

        when(processRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(processRepository.save(existing)).thenReturn(existing);

        processService.deleteProcess(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");
        verify(processRepository).findById(7L);
        verify(processRepository).save(existing);
    }

    @Test
    void findProcesses_ok() {
        Company company = new Company();
        company.setId(10L);

        Process a = new Process(1L, "Proc X", "dx", "active", company, null, null);
        when(processRepository.findAll()).thenReturn(List.of(a));

        ProcessDTO dto = new ProcessDTO(1L, "Proc X", "dx", 10L);
        when(modelMapper.map(a, ProcessDTO.class)).thenReturn(dto);

        List<ProcessDTO> result = processService.findProcesses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Proc X");
        assertThat(result.get(0).getCompanyId()).isEqualTo(10L);

        verify(processRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Process.class), eq(ProcessDTO.class));
    }
}
