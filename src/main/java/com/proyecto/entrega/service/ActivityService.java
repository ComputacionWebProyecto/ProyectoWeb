package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.ActivityDTO;
import com.proyecto.entrega.entity.Activity;
import com.proyecto.entrega.entity.Edge;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.ActivityRepository;
import com.proyecto.entrega.repository.EdgeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ActivityService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private ProcessService processService;

    public ActivityDTO createActivity(ActivityDTO activityDTO) {
        if (activityDTO.getProcessId() == null) {
            throw new ValidationException("El ID del proceso es requerido");
        }

        // Validamos que el proceso exista
        Process process = processService.findProcessEntity(activityDTO.getProcessId());

        Activity activity = modelMapper.map(activityDTO, Activity.class);
        activity.setProcess(process);

        activity = activityRepository.save(activity);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public ActivityDTO updateActivity(ActivityDTO activityDTO) {
        if (activityDTO.getId() == null) {
            throw new ValidationException("El ID de la actividad es requerido para actualizar");
        }
        if (activityDTO.getProcessId() == null) {
            throw new ValidationException("El ID del proceso es requerido para actualizar");
        }

        // Validamos que el proceso exista
        Process process = processService.findProcessEntity(activityDTO.getProcessId());

        Activity activity = activityRepository.findById(activityDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", activityDTO.getId()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public Activity findActivityEntity(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));
    }

    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));

        // Eliminar edges conectados en cascada (soft delete)
        List<Edge> connectedEdges = edgeRepository.findByActivityId(id);
        for (Edge edge : connectedEdges) {
            edge.setStatus("inactive");
            edgeRepository.save(edge);
        }

        System.out.println("Activity eliminada: ID=" + id + ", Edges afectados: " + connectedEdges.size());

        activity.setStatus("inactive");
        activityRepository.save(activity);
    }

    public List<ActivityDTO> findActivities() {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityDTO.class))
                .toList();
    }

    /**
     * Obtiene todas las activities activas de un proceso específico.
     *
     * Este método permite filtrar activities por el proceso al que pertenecen,
     * facilitando la visualización de elementos específicos de cada proceso.
     * Solo retorna activities con status='active' debido al filtro @Where de la
     * entidad.
     *
     * @param processId Identificador del proceso
     * @return Lista de ActivityDTO de activities activas del proceso
     */
    public List<ActivityDTO> findActivitiesByProcess(Long processId) {
        List<Activity> activities = activityRepository.findByProcessId(processId);

        System.out.println("Activities activas del proceso " + processId + ": " + activities.size());

        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityDTO.class))
                .toList();
    }

    /**
     * Obtiene todas las activities inactivas de un proceso específico.
     *
     * Este método permite visualizar activities que fueron eliminadas mediante
     * soft delete de un proceso específico para decidir cuáles reactivar.
     * Utiliza una query personalizada que ignora el filtro @Where de la entidad.
     *
     * @param processId Identificador del proceso
     * @return Lista de ActivityDTO de activities inactivas del proceso
     */
    public List<ActivityDTO> findInactiveActivitiesByProcess(Long processId) {
        List<Activity> activities = activityRepository.findByProcessIdAndStatus(processId, "inactive");

        System.out.println("Activities inactivas del proceso " + processId + ": " + activities.size());
        activities.forEach(a -> System.out.println("  - ID: " + a.getId() + ", Name: " + a.getName()));

        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityDTO.class))
                .toList();
    }

    /**
     * Reactiva una activity que fue marcada como inactiva mediante soft delete.
     *
     * Este método permite recuperar activities eliminadas cambiando su estado
     * de 'inactive' a 'active'. La activity volverá a ser visible en el proceso.
     *
     * El método busca la activity por id sin importar su estado usando el
     * repositorio directamente para evitar el filtro @Where.
     *
     * @param id Identificador de la activity a reactivar
     * @return ActivityDTO de la activity reactivada
     * @throws EntityNotFoundException si la activity no existe
     */
    public ActivityDTO reactivateActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));

        activity.setStatus("active");
        activity = activityRepository.save(activity);

        System.out.println("Activity reactivada: ID=" + id + ", Name=" + activity.getName());

        return modelMapper.map(activity, ActivityDTO.class);
    }

}
