package com.proyecto.entrega.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.entrega.exception.UnauthorizedAccessException;

import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.service.ActivityService;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping()
    public ActivityDTO createActivity(Authentication authentication, @RequestBody ActivityDTO activity) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return activityService.createActivity(activity);
    }

    @PutMapping()
    public ActivityDTO updateActivity(Authentication authentication, @RequestBody ActivityDTO activity) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return activityService.updateActivity(activity);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteActivity(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        activityService.deleteActivity(id);
    }

    @GetMapping(value = "/{id}")
    public ActivityDTO getActivity(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return activityService.findActivity(id);
    }

    @GetMapping()
    public List<ActivityDTO> getActivities(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return activityService.findActivities();
    }

    /**
     * Obtiene todas las activities de un proceso específico.
     *
     * Este endpoint permite filtrar activities por el proceso al que pertenecen,
     * facilitando la visualización de elementos específicos de cada proceso.
     * Solo retorna activities con status='active'.
     *
     * Endpoint: GET /api/activity/process/{processId}
     *
     * @param processId Identificador del proceso
     * @return Lista de activities activas del proceso
     */
    @GetMapping(value = "/process/{processId}")
    public List<ActivityDTO> getActivitiesByProcess(Authentication authentication, @PathVariable Long processId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return activityService.findActivitiesByProcess(processId);
    }

    /**
     * Obtiene todas las activities inactivas de un proceso específico.
     *
     * Este endpoint permite visualizar activities eliminadas mediante soft delete
     * de un proceso específico, para decidir cuáles reactivar.
     *
     * Endpoint: GET /api/activity/process/{processId}/inactive
     *
     * @param processId Identificador del proceso
     * @return Lista de activities inactivas del proceso
     */
    @GetMapping(value = "/process/{processId}/inactive")
    public List<ActivityDTO> getInactiveActivitiesByProcess(Authentication authentication, @PathVariable Long processId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return activityService.findInactiveActivitiesByProcess(processId);
    }

    /**
     * Reactiva una activity que fue marcada como inactiva mediante soft delete.
     *
     * Este endpoint permite recuperar activities eliminadas cambiando su estado
     * de 'inactive' a 'active', haciéndolas visibles nuevamente en el proceso.
     *
     * Endpoint: PUT /api/activity/{id}/reactivate
     *
     * @param id Identificador de la activity a reactivar
     * @return ActivityDTO de la activity reactivada
     */
    @PutMapping(value = "/{id}/reactivate")
    public ActivityDTO reactivateActivity(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return activityService.reactivateActivity(id);
    }

}
