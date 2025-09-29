package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.repository.ActivityRepository;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    ActivityRepository activityRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    ActivityService activityService;

    @Test
    void createActivity_ok() {
        ActivityDTO inDto = new ActivityDTO(null, "A1", 10.0, 20.0, "desc", 100.0, 50.0, "active");
        Activity entityBefore = new Activity(null, "A1", 10.0, 20.0, "desc", 100.0, 50.0, "active", null);
        Activity entitySaved  = new Activity(1L,   "A1", 10.0, 20.0, "desc", 100.0, 50.0, "active", null);
        ActivityDTO outDto    = new ActivityDTO(1L, "A1", 10.0, 20.0, "desc", 100.0, 50.0, "active");

        when(modelMapper.map(inDto, Activity.class)).thenReturn(entityBefore);
        when(activityRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, ActivityDTO.class)).thenReturn(outDto);

        ActivityDTO result = activityService.createActivity(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("A1");
        verify(activityRepository).save(entityBefore);
    }

    @Test
    void updateActivity_ok() {
        ActivityDTO inDto = new ActivityDTO(5L, "Edit", 1.0, 2.0, "d", 3.0, 4.0, "active");
        Activity entity = new Activity(5L, "Edit", 1.0, 2.0, "d", 3.0, 4.0, "active", null);

        when(modelMapper.map(inDto, Activity.class)).thenReturn(entity);
        when(activityRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, ActivityDTO.class)).thenReturn(inDto);

        ActivityDTO result = activityService.updateActivity(inDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        verify(activityRepository).save(entity);
    }

    @Test
    void findActivity_found() {
        Activity entity = new Activity(2L, "A2", 1.0, 2.0, "d", 10.0, 20.0, "active", null);
        ActivityDTO dto = new ActivityDTO(2L, "A2", 1.0, 2.0, "d", 10.0, 20.0, "active");

        when(activityRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ActivityDTO.class)).thenReturn(dto);

        ActivityDTO result = activityService.findActivity(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("A2");
    }

    @Test
    void findActivity_notFound_throwsEntityNotFound() {
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.findActivity(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(activityRepository).findById(999L);
        verifyNoMoreInteractions(activityRepository);
    }

    @Test
    void deleteActivity_ok() {
        activityService.deleteActivity(7L);
        verify(activityRepository).deleteById(7L);
    }

    @Test
    void findActivities_ok() {
        Activity a = new Activity(1L, "A", 1.0, 2.0, "d", 3.0, 4.0, "active", null);
        when(activityRepository.findAll()).thenReturn(List.of(a));
        ActivityDTO dto = new ActivityDTO(1L, "A", 1.0, 2.0, "d", 3.0, 4.0, "active");
        when(modelMapper.map(a, ActivityDTO.class)).thenReturn(dto);

        List<ActivityDTO> result = activityService.findActivities();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("A");
        verify(activityRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Activity.class), eq(ActivityDTO.class));
    }
}