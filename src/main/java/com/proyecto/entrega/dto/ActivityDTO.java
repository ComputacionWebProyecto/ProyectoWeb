package com.proyecto.entrega.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.proyecto.entrega.entity.Edge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {

    private Long id;
    private String name;
    private Double x;
    private Double y;
    private String description;
    private Double width;
    private Double height;
    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long processId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ProcessDTO process;

    @JsonIgnore
    private List<Edge> outgoingEdges;
    @JsonIgnore  
    private List<Edge> incomingEdges; 
    
}
