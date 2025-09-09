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

import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.service.GatewayService;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    @Autowired
    private GatewayService gatewayService;

    @PostMapping()
    public void createGateway(@RequestBody GatewayDTO gatewayDTO) {
        gatewayService.createGateway(gatewayDTO);
    }

    @PutMapping()
    public void updateGateway(@RequestBody GatewayDTO gatewayDTO) {
        gatewayService.updateGateway(gatewayDTO);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteGateway(@PathVariable Long id) {
        gatewayService.deleteGateway(id);
    }

    @GetMapping(value = "/{id}")
    public GatewayDTO getGateway(@PathVariable Long id) {
        return gatewayService.findGateway(id);
    }

    @GetMapping()
    public List<GatewayDTO> getGateways() {
        return gatewayService.findGateways();
    }

}
