package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.ActivityRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ActivityService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProcessService processService;

    public ActivityDTO createActivity(ActivityDTO activityDTO) {
        if (activityDTO.getProcessId() == null) {
            throw new IllegalArgumentException("ProcessId is required");
        }

        // Validamos que el proceso exista
        Process process = modelMapper.map(processService.findProcess(activityDTO.getProcessId()), Process.class);

        Activity activity = modelMapper.map(activityDTO, Activity.class);
        activity.setProcess(process);

        activity = activityRepository.save(activity);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public ActivityDTO updateActivity(ActivityDTO activityDTO) {
        if (activityDTO.getId() == null) {
            throw new IllegalArgumentException("ActivityId is required for update");
        }
        if (activityDTO.getProcessId() == null) {
            throw new IllegalArgumentException("ProcessId is required for update");
        }

        // Validamos que el proceso exista
        Process process = modelMapper.map(processService.findProcess(activityDTO.getProcessId()), Process.class);


        Activity activity = activityRepository.findById(activityDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity " + activityDTO.getId() + " not found"));

        // Actualizamos los campos
        activity.setName(activityDTO.getName());
        activity.setX(activityDTO.getX());
        activity.setY(activityDTO.getY());
        activity.setDescription(activityDTO.getDescription());
        activity.setWidth(activityDTO.getWidth());
        activity.setHeight(activityDTO.getHeight());
        activity.setProcess(process);

        activity = activityRepository.save(activity);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public ActivityDTO findActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity " + id + " not found"));
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity " + id + " not found"));
        activity.setStatus("inactive"); 
        activityRepository.save(activity);
    }

    public List<ActivityDTO> findActivities() {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityDTO.class))
                .toList();
    }

}
