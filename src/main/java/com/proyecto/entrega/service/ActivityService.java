package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.repository.ActivityRepository;

@Service
public class ActivityService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ActivityRepository activityRepository;

    public ActivityDTO createActivity(ActivityDTO activityDTO) {
        Activity activity = modelMapper.map(activityDTO, Activity.class);
        activity = activityRepository.save(activity);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public ActivityDTO updateActivity(ActivityDTO activityDTO) {
        Activity activity = modelMapper.map(activityDTO, Activity.class);
        activity = activityRepository.save(activity);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public ActivityDTO findActivity(Long id) {
        Activity activity = activityRepository.findById(id).orElse(null);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public List<ActivityDTO> findActivities() {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityDTO.class))
                .toList();
    }

}
