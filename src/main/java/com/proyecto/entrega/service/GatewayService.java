package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.entity.Gateway;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.GatewayRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GatewayService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GatewayRepository gatewayRepository;

    @Autowired
    private ProcessService ProcessService;

    // Crear Gateway
    public GatewayDTO createGateway(GatewayDTO gatewayDTO) {
        if (gatewayDTO.getProcessId() == null) {
            throw new IllegalArgumentException("El processId no puede ser null");
        }

        // Validar que el proceso exista
        Process process = modelMapper.map(ProcessService.findProcess(gatewayDTO.getProcessId()), Process.class);

        Gateway gateway = modelMapper.map(gatewayDTO, Gateway.class);

        gateway.setProcess(process);

        gateway = gatewayRepository.save(gateway);
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    // Actualizar Gateway
    public GatewayDTO updateGateway(GatewayDTO gatewayDTO) {
        if (gatewayDTO.getId() == null) {
            throw new IllegalArgumentException("El id del Gateway no puede ser null para actualizar");
        }
        if (gatewayDTO.getProcessId() == null) {
            throw new IllegalArgumentException("El processId no puede ser null");
        }

        // Verificar que el gateway exista
        Gateway existingGateway = gatewayRepository.findById(gatewayDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + gatewayDTO.getId() + " not found"));

        // Validar que el proceso exista
        Process process = modelMapper.map(ProcessService.findProcess(gatewayDTO.getProcessId()), Process.class);

        existingGateway.setProcess(process);

        existingGateway = gatewayRepository.save(existingGateway);
        return modelMapper.map(existingGateway, GatewayDTO.class);
    }

    // Buscar Gateway por ID
    public GatewayDTO findGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    // Eliminar (lógicamente) Gateway
    public void deleteGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));
        gateway.setStatus("inactive"); // Asumo que Gateway tiene un campo status
        gatewayRepository.save(gateway);
    }

    // Listar todos los Gateways
    public List<GatewayDTO> findGateways() {
        List<Gateway> gateways = gatewayRepository.findAll();
        return gateways.stream()
                .map(gateway -> modelMapper.map(gateway, GatewayDTO.class))
                .toList();
    }
}
