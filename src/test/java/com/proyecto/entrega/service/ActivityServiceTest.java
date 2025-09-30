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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock ActivityRepository activityRepository;
    @Mock ModelMapper modelMapper;

    @InjectMocks ActivityService activityService;

    @Test
    void create_ok() {
        ActivityDTO in = new ActivityDTO(null, "A1", 1.0,2.0,"d",10.0,5.0,"active");
        Activity before = new Activity();
        Activity saved  = new Activity();
        saved.setId(1L);
        ActivityDTO out = new ActivityDTO(1L,"A1",1.0,2.0,"d",10.0,5.0,"active");

        when(modelMapper.map(in, Activity.class)).thenReturn(before);
        when(activityRepository.save(before)).thenReturn(saved);
        when(modelMapper.map(saved, ActivityDTO.class)).thenReturn(out);

        ActivityDTO result = activityService.createActivity(in);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void update_ok() {
        ActivityDTO in = new ActivityDTO(5L,"E",1.0,2.0,"d",10.0,5.0,"active");
        Activity entity = new Activity();
        Activity saved = new Activity();
        saved.setId(5L);
        ActivityDTO out = new ActivityDTO(5L,"E",1.0,2.0,"d",10.0,5.0,"active");

        when(modelMapper.map(in, Activity.class)).thenReturn(entity);
        when(activityRepository.save(entity)).thenReturn(saved);
        when(modelMapper.map(saved, ActivityDTO.class)).thenReturn(out);

        ActivityDTO result = activityService.updateActivity(in);

        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void findById_ok() {
        Activity entity = new Activity();
        entity.setId(7L);
        ActivityDTO dto = new ActivityDTO(7L,"A",1.0,2.0,"d",10.0,5.0,"active");

        when(activityRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ActivityDTO.class)).thenReturn(dto);

        ActivityDTO result = activityService.findActivity(7L);
        assertThat(result.getId()).isEqualTo(7L);
    }

    @Test
    void findById_notFound() {
        when(activityRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> activityService.findActivity(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_ok() {
        activityService.deleteActivity(3L);
        verify(activityRepository).deleteById(3L);
    }

    @Test
    void list_ok() {
        when(activityRepository.findAll()).thenReturn(List.of(new Activity(), new Activity()));
        when(modelMapper.map(any(Activity.class), eq(ActivityDTO.class)))
                .thenReturn(new ActivityDTO(), new ActivityDTO());

        List<ActivityDTO> result = activityService.findActivities();
        assertThat(result).hasSize(2);
    }
}
