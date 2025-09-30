package com.proyecto.entrega.dto;

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
    private Long processId;
    
}
