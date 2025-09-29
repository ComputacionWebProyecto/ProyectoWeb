package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.entity.Gateway;
import com.proyecto.entrega.repository.GatewayRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GatewayService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private GatewayRepository gatewayRepository;

    public GatewayDTO createGateway(GatewayDTO gatewayDTO) {
        Gateway gateway = modelMapper.map(gatewayDTO, Gateway.class);
        gateway = gatewayRepository.save(gateway);
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    public GatewayDTO updateGateway(GatewayDTO gatewayDTO) {
        Gateway gateway = modelMapper.map(gatewayDTO, Gateway.class);
        gateway = gatewayRepository.save(gateway);
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    public GatewayDTO findGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    public void deleteGateway(Long id) {
        gatewayRepository.deleteById(id);
    }

    public List<GatewayDTO> findGateways() {
        List<Gateway> gateways = gatewayRepository.findAll();
        return gateways.stream()
                .map(gateway -> modelMapper.map(gateway, GatewayDTO.class))
                .toList();
    }

}
