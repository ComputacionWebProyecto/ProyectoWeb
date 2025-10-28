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
public class GatewayDTO {

    private Long id;
    private String type;
    private String status;
    private Double x;
    private Double y;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long processId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ProcessDTO process;

}

 
