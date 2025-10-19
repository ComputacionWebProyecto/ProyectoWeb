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

import com.proyecto.entrega.dto.EdgeDTO;
import com.proyecto.entrega.service.EdgeService;

@RestController
@RequestMapping("/api/edge")
public class EdgeController {

    @Autowired
    private EdgeService edgeService;

    @PostMapping()
    public EdgeDTO createEdge(@RequestBody EdgeDTO edgeDTO) {
        return edgeService.createEdge(edgeDTO);
    }

    @PutMapping()
    public EdgeDTO updateEdge(@RequestBody EdgeDTO edgeDTO) {
        return edgeService.updateEdge(edgeDTO);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEdge(@PathVariable Long id) {
        edgeService.deleteEdge(id);
    }

    @GetMapping(value = "/{id}")
    public EdgeDTO getEdge(@PathVariable Long id) {
        return edgeService.findEdge(id);
    }

    @GetMapping()
    public List<EdgeDTO> getEdges() {
        return edgeService.findEdges();
    }

}
