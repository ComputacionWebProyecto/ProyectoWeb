package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.ActivityRepository;
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
class ActivityServiceTest {

    @Mock ActivityRepository activityRepository;
    @Mock ModelMapper modelMapper;
    @Mock ProcessService processService;

    @InjectMocks ActivityService activityService;

    // Helpers
    private ActivityDTO dto(Long id, String name, Double x, Double y, String d, Double w, Double h, String status, Long processId) {
        ActivityDTO a = new ActivityDTO();
        a.setId(id); a.setName(name); a.setX(x); a.setY(y); a.setDescription(d);
        a.setWidth(w); a.setHeight(h); a.setStatus(status);
        a.setProcessId(processId);
        return a;
    }

    private Activity ent(Long id, String name, Double x, Double y, String d, Double w, Double h, String status, Process p) {
        Activity a = new Activity();
        a.setId(id); a.setName(name); a.setX(x); a.setY(y); a.setDescription(d);
        a.setWidth(w); a.setHeight(h); a.setStatus(status);
        a.setProcess(p);
        return a;
    }

    // ================= CREATE =================

    @Test
    void createActivity_ok() {
        ActivityDTO inDto = dto(null, "A1", 10.0, 20.0, "desc", 100.0, 50.0, null, 5L);

        Process proc = new Process(); proc.setId(5L);

        Activity entityBefore = ent(null, "A1", 10.0, 20.0, "desc", 100.0, 50.0, null, null);
        Activity entitySaved  = ent(1L,   "A1", 10.0, 20.0, "desc", 100.0, 50.0, "active", proc);
        ActivityDTO outDto    = dto(1L, "A1", 10.0, 20.0, "desc", 100.0, 50.0, "active", 5L);

        when(processService.findProcessEntity(5L)).thenReturn(proc);
        when(modelMapper.map(inDto, Activity.class)).thenReturn(entityBefore);
        when(activityRepository.save(any(Activity.class))).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, ActivityDTO.class)).thenReturn(outDto);

        ActivityDTO result = activityService.createActivity(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("active");

        ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository).save(captor.capture());
        Activity saved = captor.getValue();
        assertThat(saved.getProcess()).isSameAs(proc);
        assertThat(saved.getName()).isEqualTo("A1");
    }

    @Test
    void createActivity_missingProcessId_illegalArgument() {
        ActivityDTO inDto = dto(null, "A", 0.0, 0.0, "d", 1.0, 1.0, null, null);

        assertThatThrownBy(() -> activityService.createActivity(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ProcessId is required");

        verifyNoInteractions(processService, activityRepository, modelMapper);
    }

    @Test
    void createActivity_processNoExiste_entityNotFound() {
        ActivityDTO inDto = dto(null, "A", 0.0, 0.0, "d", 1.0, 1.0, null, 999L);

        when(processService.findProcessEntity(999L))
                .thenThrow(new EntityNotFoundException("Process 999 not found"));

        assertThatThrownBy(() -> activityService.createActivity(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        // Ojo: como falla antes del map, no se usa el mapper ni el repo
        verify(processService).findProcessEntity(999L);
        verifyNoInteractions(activityRepository, modelMapper);
    }

    // ================= UPDATE =================

    @Test
    void updateActivity_ok() {
        ActivityDTO inDto = dto(5L, "Edit", 1.0, 2.0, "d", 3.0, 4.0, "active", 7L);

        Process proc = new Process(); proc.setId(7L);

        Activity existing = ent(5L, "Old", 9.0, 8.0, "old", 7.0, 6.0, "active", null);
        Activity saved    = ent(5L, "Edit", 1.0, 2.0, "d", 3.0, 4.0, "active", proc);
        ActivityDTO out   = dto(5L, "Edit", 1.0, 2.0, "d", 3.0, 4.0, "active", 7L);

        when(processService.findProcessEntity(7L)).thenReturn(proc);
        when(activityRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(activityRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, ActivityDTO.class)).thenReturn(out);

        ActivityDTO result = activityService.updateActivity(inDto);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(existing.getName()).isEqualTo("Edit");
        assertThat(existing.getX()).isEqualTo(1.0);
        assertThat(existing.getY()).isEqualTo(2.0);
        assertThat(existing.getDescription()).isEqualTo("d");
        assertThat(existing.getWidth()).isEqualTo(3.0);
        assertThat(existing.getHeight()).isEqualTo(4.0);
        assertThat(existing.getProcess()).isSameAs(proc);

        verify(activityRepository).save(existing);
    }

    @Test
    void updateActivity_missingId_illegalArgument() {
        ActivityDTO inDto = dto(null, "X", 0.0, 0.0, "d", 1.0, 1.0, "active", 1L);

        assertThatThrownBy(() -> activityService.updateActivity(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ActivityId is required");

        verifyNoInteractions(processService, activityRepository, modelMapper);
    }

    @Test
    void updateActivity_missingProcessId_illegalArgument() {
        ActivityDTO inDto = dto(5L, "X", 0.0, 0.0, "d", 1.0, 1.0, "active", null);

        assertThatThrownBy(() -> activityService.updateActivity(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ProcessId is required");

        verifyNoInteractions(processService, activityRepository, modelMapper);
    }

    @Test
    void updateActivity_noExisteActivity_entityNotFound() {
        // El service primero valida el process, así que sí hay que stubearlo
        when(processService.findProcessEntity(7L)).thenReturn(new Process());
        when(activityRepository.findById(77L)).thenReturn(Optional.empty());

        ActivityDTO inDto = dto(77L, "X", 0.0, 0.0, "d", 1.0, 1.0, "active", 7L);

        assertThatThrownBy(() -> activityService.updateActivity(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("77");

        verify(processService).findProcessEntity(7L);
        verify(activityRepository).findById(77L);
        verifyNoMoreInteractions(activityRepository);
    }

    // ================= FIND =================

    @Test
    void findActivity_found() {
        Process p = new Process(); p.setId(3L);
        Activity entity = ent(2L, "A2", 1.0, 2.0, "d", 10.0, 20.0, "active", p);
        ActivityDTO dto = dto(2L, "A2", 1.0, 2.0, "d", 10.0, 20.0, "active", 3L);

        when(activityRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ActivityDTO.class)).thenReturn(dto);

        ActivityDTO result = activityService.findActivity(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("A2");
        assertThat(result.getStatus()).isEqualTo("active");
    }

    @Test
    void findActivity_notFound_entityNotFound() {
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.findActivity(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(activityRepository).findById(999L);
        verifyNoMoreInteractions(activityRepository);
    }

    // ================= DELETE (soft) =================

    @Test
    void deleteActivity_ok_softDelete() {
        Activity entity = ent(7L, "A", 1.0, 2.0, "d", 3.0, 4.0, "active", null);
        when(activityRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(activityRepository.save(entity)).thenReturn(entity);

        activityService.deleteActivity(7L);

        assertThat(entity.getStatus()).isEqualTo("inactive");
        verify(activityRepository).save(entity);
    }

    @Test
    void deleteActivity_notFound_entityNotFound() {
        when(activityRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.deleteActivity(404L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("404");

        verify(activityRepository).findById(404L);
        verifyNoMoreInteractions(activityRepository);
    }

    // ================= LIST =================

    @Test
    void findActivities_ok() {
        Process p = new Process(); p.setId(2L);
        Activity a = ent(1L, "A", 1.0, 2.0, "d", 3.0, 4.0, "active", p);
        when(activityRepository.findAll()).thenReturn(List.of(a));

        ActivityDTO dto = dto(1L, "A", 1.0, 2.0, "d", 3.0, 4.0, "active", 2L);
        when(modelMapper.map(a, ActivityDTO.class)).thenReturn(dto);

        List<ActivityDTO> result = activityService.findActivities();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(0).getStatus()).isEqualTo("active");
        verify(activityRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Activity.class), eq(ActivityDTO.class));
    }
}
