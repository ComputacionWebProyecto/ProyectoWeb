package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.dto.ProcessSummaryDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.ProcessRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
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
    @Mock CompanyService companyService;   // <- ahora depende del service, NO del repo
    @Mock ModelMapper modelMapper;

    @InjectMocks ProcessService processService;

    // -------- helpers
    private Process proc(Long id, String name, String desc, String status, Company c) {
        Process p = new Process();
        p.setId(id);
        p.setName(name);
        p.setDescription(desc);
        p.setStatus(status);
        p.setCompany(c);
        return p;
    }

    private Company comp(Long id) {
        Company c = new Company();
        c.setId(id);
        return c;
    }

    private ProcessDTO dto(Long id, String name, String status, String desc, Long companyId) {
        ProcessDTO d = new ProcessDTO();
        d.setId(id);
        d.setName(name);
        d.setStatus(status);
        d.setDescription(desc);
        d.setCompanyId(companyId);
        return d;
    }

    // =================== CREATE ===================

    @Test
    void createProcess_ok() {
        ProcessDTO inDto = dto(null, "Proc A", null, "desc A", 77L);

        Company company = comp(77L);
        Process mapped  = proc(null, "Proc A", "desc A", "active", null);
        Process saved   = proc(1L, "Proc A", "desc A", "active", company);
        ProcessDTO out  = dto(1L, "Proc A", "active", "desc A", 77L);

        when(modelMapper.map(inDto, Process.class)).thenReturn(mapped);
        when(companyService.findCompanyEntity(77L)).thenReturn(company);
        when(processRepository.save(any(Process.class))).thenReturn(saved);
        when(modelMapper.map(saved, ProcessDTO.class)).thenReturn(out);

        ProcessDTO result = processService.createProcess(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Proc A");
        assertThat(result.getCompanyId()).isEqualTo(77L);

        ArgumentCaptor<Process> captor = ArgumentCaptor.forClass(Process.class);
        verify(processRepository).save(captor.capture());
        assertThat(captor.getValue().getCompany()).isSameAs(company);
    }

    @Test
    void createProcess_companyIdNull_illegalArgument() {
        ProcessDTO inDto = dto(null, "X", null, "d", null);

        assertThatThrownBy(() -> processService.createProcess(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CompanyId is required");

        verifyNoInteractions(processRepository, companyService, modelMapper);
    }

    // =================== UPDATE ===================

    @Test
    void updateProcess_ok() {
        ProcessDTO inDto = dto(10L, "Proc Edit", null, "d", 77L);

        Company company = comp(77L);
        Process existing = proc(10L, "Old", "old", "active", null);
        Process saved    = proc(10L, "Proc Edit", "d", "active", company);
        ProcessDTO out   = dto(10L, "Proc Edit", "active", "d", 77L);

        when(processRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(companyService.findCompanyEntity(77L)).thenReturn(company);
        when(processRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, ProcessDTO.class)).thenReturn(out);

        ProcessDTO result = processService.updateProcess(inDto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(existing.getName()).isEqualTo("Proc Edit");
        assertThat(existing.getDescription()).isEqualTo("d");
        assertThat(existing.getCompany()).isSameAs(company);
        verify(processRepository).save(existing);
    }

    @Test
    void updateProcess_idNull_illegalArgument() {
        ProcessDTO inDto = dto(null, "X", null, "d", 1L);

        assertThatThrownBy(() -> processService.updateProcess(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id is required");

        verifyNoInteractions(processRepository, companyService, modelMapper);
    }

    @Test
    void updateProcess_companyIdNull_illegalArgument() {
        ProcessDTO inDto = dto(10L, "X", null, "d", null);

        assertThatThrownBy(() -> processService.updateProcess(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CompanyId is required");

        verifyNoInteractions(companyService);
    }

    @Test
    void updateProcess_noExiste_entityNotFound() {
        ProcessDTO inDto = dto(99L, "X", null, "d", 77L);
        when(processRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processService.updateProcess(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Process 99");

        verify(processRepository).findById(99L);
        verifyNoMoreInteractions(processRepository);
        verifyNoInteractions(companyService, modelMapper);
    }

    // =================== FIND (DTO & ENTITY) ===================

    @Test
    void findProcess_found() {
        Company c = comp(50L);
        Process entity = proc(2L, "Proc B", "desc B", "active", c);
        ProcessDTO dto = dto(2L, "Proc B", "active", "desc B", 50L);

        when(processRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ProcessDTO.class)).thenReturn(dto);

        ProcessDTO result = processService.findProcess(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getCompanyId()).isEqualTo(50L);
    }

    @Test
    void findProcess_notFound() {
        when(processRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processService.findProcess(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findProcessEntity_found_y_notFound() {
        Process p = proc(3L, "P", "D", "active", null);
        when(processRepository.findById(3L)).thenReturn(Optional.of(p));
        assertThat(processService.findProcessEntity(3L).getId()).isEqualTo(3L);

        when(processRepository.findById(4L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> processService.findProcessEntity(4L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("4");
    }

    // =================== DELETE (soft) ===================

    @Test
    void deleteProcess_ok_softDelete() {
        Process existing = proc(7L, "X", "Y", "active", null);
        when(processRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(processRepository.save(existing)).thenReturn(existing);

        processService.deleteProcess(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");
        ArgumentCaptor<Process> captor = ArgumentCaptor.forClass(Process.class);
        verify(processRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("inactive");
    }

    @Test
    void deleteProcess_notFound() {
        when(processRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processService.deleteProcess(404L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("404");

        verify(processRepository).findById(404L);
        verifyNoMoreInteractions(processRepository);
    }

    // =================== LISTS ===================

    @Test
    void findProcesses_ok() {
        Company c = comp(10L);
        Process a = proc(1L, "Proc X", "dx", "active", c);
        Process b = proc(2L, "Proc Y", "dy", "active", c);
        when(processRepository.findAll()).thenReturn(List.of(a, b));

        ProcessDTO da = dto(1L, "Proc X", "active", "dx", 10L);
        ProcessDTO db = dto(2L, "Proc Y", "active", "dy", 10L);
        when(modelMapper.map(a, ProcessDTO.class)).thenReturn(da);
        when(modelMapper.map(b, ProcessDTO.class)).thenReturn(db);

        List<ProcessDTO> result = processService.findProcesses();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Proc X");
        assertThat(result.get(1).getName()).isEqualTo("Proc Y");
        verify(processRepository).findAll();
        verify(modelMapper, atLeast(2)).map(any(Process.class), eq(ProcessDTO.class));
    }

    @Test
    void getProcessesByCompany_ok() {
        Company c = comp(77L);
        Process p1 = proc(1L, "A", "da", "active", c);
        Process p2 = proc(2L, "B", "db", "active", c);
        when(processRepository.findByCompanyId(77L)).thenReturn(List.of(p1, p2));

        ProcessDTO d1 = dto(1L, "A", "active", "da", 77L);
        ProcessDTO d2 = dto(2L, "B", "active", "db", 77L);
        when(modelMapper.map(p1, ProcessDTO.class)).thenReturn(d1);
        when(modelMapper.map(p2, ProcessDTO.class)).thenReturn(d2);

        List<ProcessDTO> result = processService.getProcessesByCompany(77L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCompanyId()).isEqualTo(77L);
        verify(processRepository).findByCompanyId(77L);
    }

    @Test
    void getProcessesSummaryByCompany_ok() {
        Company c = comp(77L);
        Process p1 = proc(1L, "S1", "ds1", "active", c);
        Process p2 = proc(2L, "S2", "ds2", "active", c);
        when(processRepository.findByCompanyId(77L)).thenReturn(List.of(p1, p2));

        List<ProcessSummaryDTO> result = processService.getProcessesSummaryByCompany(77L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("S1");
        assertThat(result.get(0).getDescription()).isEqualTo("ds1");
        assertThat(result.get(1).getName()).isEqualTo("S2");
        // no usa ModelMapper en este método
        verify(processRepository).findByCompanyId(77L);
        verifyNoInteractions(modelMapper);
    }
}
