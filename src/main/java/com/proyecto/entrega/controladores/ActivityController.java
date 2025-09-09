package com.proyecto.entrega.controladores;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.service.ActivityService;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping()
    public void createActivity(@RequestBody ActivityDTO activity) {
        activityService.createActivity(activity);
    }

    @PutMapping()
    public void updateActivity(@RequestBody ActivityDTO activity) {
        activityService.updateActivity(activity);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
    }

    @GetMapping(value = "/{id}")
    public ActivityDTO getActivity(@PathVariable Long id) {
        return activityService.findActivity(id);
    }

    @GetMapping()
    public List<ActivityDTO> getActivities() {
        return activityService.findActivities();
    }

}
