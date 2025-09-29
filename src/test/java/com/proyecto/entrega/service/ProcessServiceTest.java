package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.ProcessDTO;
import com.proyecto.entrega.entity.Process;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessServiceTest {

    @Mock
    ProcessRepository processRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    ProcessService processService;

    @Test
    void createProcess_ok() {
        ProcessDTO inDto = new ProcessDTO(null, "Proc A", "desc A", "active");
        Process entityBefore = new Process(null, "Proc A", "desc A", "active", null, null, null);
        Process entitySaved  = new Process(1L,   "Proc A", "desc A", "active", null, null, null);
        ProcessDTO outDto    = new ProcessDTO(1L, "Proc A", "desc A", "active");

        when(modelMapper.map(inDto, Process.class)).thenReturn(entityBefore);
        when(processRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, ProcessDTO.class)).thenReturn(outDto);

        ProcessDTO result = processService.createProcess(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Proc A");
        verify(processRepository).save(entityBefore);
    }

    @Test
    void updateProcess_ok() {
        ProcessDTO inDto = new ProcessDTO(10L, "Proc Edit", "d", "inactive");
        Process entity = new Process(10L, "Proc Edit", "d", "inactive", null, null, null);

        when(modelMapper.map(inDto, Process.class)).thenReturn(entity);
        when(processRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, ProcessDTO.class)).thenReturn(inDto);

        ProcessDTO result = processService.updateProcess(inDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo("inactive");
        verify(processRepository).save(entity);
    }

    @Test
    void findProcess_found() {
        Process entity = new Process(2L, "Proc B", "desc B", "active", null, null, null);
        ProcessDTO dto = new ProcessDTO(2L, "Proc B", "desc B", "active");

        when(processRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ProcessDTO.class)).thenReturn(dto);

        ProcessDTO result = processService.findProcess(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Proc B");
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
        processService.deleteProcess(7L);
        verify(processRepository).deleteById(7L);
    }

    @Test
    void findProcesses_ok() {
        Process a = new Process(1L, "Proc X", "dx", "active", null, null, null);
        when(processRepository.findAll()).thenReturn(List.of(a));

        ProcessDTO dto = new ProcessDTO(1L, "Proc X", "dx", "active");
        when(modelMapper.map(a, ProcessDTO.class)).thenReturn(dto);

        List<ProcessDTO> result = processService.findProcesses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Proc X");
        verify(processRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Process.class), eq(ProcessDTO.class));
    }
}
