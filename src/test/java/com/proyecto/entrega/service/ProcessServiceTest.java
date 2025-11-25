package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.dto.ProcessSummaryDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.ProcessRepository;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessServiceTest {

    @Mock ProcessRepository processRepository;
    @Mock CompanyService companyService;
    @Mock ModelMapper modelMapper;

    @InjectMocks ProcessService processService;

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

    

    @Test
    void createProcess_ok() {
        ProcessDTO inDto = dto(null, "Proc A", null, "desc A", 77L);

        Company company = comp(77L);
        Process mapped  = proc(null, "Proc A", "desc A", "active", null);
        Process saved   = proc(1L, "Proc A", "desc A", "active", company);
        ProcessDTO out  = dto(1L, "Proc A", "active", "desc A", 77L);

        when(processRepository.existsByNameAndCompanyId("Proc A", 77L)).thenReturn(false);
        when(modelMapper.map(inDto, Process.class)).thenReturn(mapped);
        when(companyService.findCompanyEntity(77L)).thenReturn(company);
        when(processRepository.save(any(Process.class))).thenReturn(saved);
        when(modelMapper.map(saved, ProcessDTO.class)).thenReturn(out);

        ProcessDTO result = processService.createProcess(inDto);

        assertThat(result.getId()).isEqualTo(1L);

        ArgumentCaptor<Process> captor = ArgumentCaptor.forClass(Process.class);
        verify(processRepository).save(captor.capture());

        assertThat(captor.getValue().getCompany()).isSameAs(company);
    }

    @Test
    void createProcess_companyIdNull_illegalArgument() {
        ProcessDTO inDto = dto(null, "X", null, "d", null);

        assertThatThrownBy(() -> processService.createProcess(inDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("El ID de la compañía es requerido");

        verifyNoInteractions(processRepository, companyService, modelMapper);
    }

    

    @Test
    void updateProcess_ok() {
        ProcessDTO inDto = dto(10L, "Proc Edit", null, "d", 77L);

        Company company = comp(77L);
        Process existing = proc(10L, "Old", "old", "active", null);
        Process saved    = proc(10L, "Proc Edit", "d", "active", company);
        ProcessDTO out   = dto(10L, "Proc Edit", "active", "d", 77L);

        when(processRepository.findById(10L)).thenReturn(Optional.of(existing));

        
        when(processRepository.findByCompanyId(77L)).thenReturn(List.of(existing));

        when(companyService.findCompanyEntity(77L)).thenReturn(company);
        when(processRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, ProcessDTO.class)).thenReturn(out);

        ProcessDTO result = processService.updateProcess(inDto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(existing.getCompany()).isSameAs(company);
    }

    @Test
    void updateProcess_idNull_illegalArgument() {
        ProcessDTO inDto = dto(null, "X", null, "d", 1L);

        assertThatThrownBy(() -> processService.updateProcess(inDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("El ID es requerido para actualizar");

        verifyNoInteractions(processRepository, companyService, modelMapper);
    }

    @Test
    void updateProcess_companyIdNull_illegalArgument() {
        ProcessDTO inDto = dto(10L, "X", null, "d", null);

        assertThatThrownBy(() -> processService.updateProcess(inDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("El ID de la compañía es requerido");

        verifyNoInteractions(companyService);
    }

    @Test
    void updateProcess_noExiste_entityNotFound() {
        ProcessDTO inDto = dto(99L, "X", null, "d", 77L);

        when(processRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processService.updateProcess(inDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Proceso")
                .hasMessageContaining("99");

        verify(processRepository).findById(99L);
        verifyNoInteractions(companyService, modelMapper);
    }

    

    @Test
    void findProcess_found() {
        Company c = comp(50L);
        Process entity = proc(2L, "Proc B", "desc B", "active", c);
        ProcessDTO dto = dto(2L, "Proc B", "active", "desc B", 50L);

        when(processRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ProcessDTO.class)).thenReturn(dto);

        ProcessDTO result = processService.findProcess(2L);

        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    void findProcess_notFound() {
        when(processRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processService.findProcess(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findProcessEntity_found_y_noFound() {
        Process p = proc(3L, "P", "D", "active", null);
        when(processRepository.findById(3L)).thenReturn(Optional.of(p));
        assertThat(processService.findProcessEntity(3L).getId()).isEqualTo(3L);

        when(processRepository.findById(4L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> processService.findProcessEntity(4L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("4");
    }

    

    @Test
    void deleteProcess_ok_softDelete() {
        Process existing = proc(7L, "X", "Y", "active", null);

        when(processRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(processRepository.save(existing)).thenReturn(existing);

        processService.deleteProcess(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");
        verify(processRepository).save(existing);
    }

    @Test
    void deleteProcess_notFound() {
        when(processRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processService.deleteProcess(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("404");
    }

    

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
    }

    @Test
    void getProcessesSummaryByCompany_ok() {
        Company c = comp(77L);
        Process p1 = proc(1L, "S1", "ds1", "active", c);
        Process p2 = proc(2L, "S2", "ds2", "active", c);

        when(processRepository.findByCompanyId(77L)).thenReturn(List.of(p1, p2));

        List<ProcessSummaryDTO> result = processService.getProcessesSummaryByCompany(77L);

        assertThat(result).hasSize(2);
        verifyNoInteractions(modelMapper);
    }
}
