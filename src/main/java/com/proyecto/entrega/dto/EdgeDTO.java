package com.proyecto.entrega.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class EdgeDTO {
    private Long id;
    private String description;
    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long processId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long activitySourceId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long activityDestinyId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ProcessDTO process;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ActivityDTO activitySource;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ActivityDTO activityDestiny;
}
