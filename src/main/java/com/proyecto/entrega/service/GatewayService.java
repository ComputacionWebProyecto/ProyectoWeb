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
    private ProcessService processService;

    // Crear Gateway
    public GatewayDTO createGateway(GatewayDTO gatewayDTO) {
        Gateway gateway = modelMapper.map(gatewayDTO, Gateway.class);

        if (gateway.getStatus() == null) {
            gateway.setStatus("active");
        }

        if (gatewayDTO.getProcessId() != null) {
            Process process = processService.findProcessEntity(gatewayDTO.getProcessId());
            gateway.setProcess(process);
        }

        gateway.setX(gatewayDTO.getX());
        gateway.setY(gatewayDTO.getY());

        gateway = gatewayRepository.save(gateway);
        
        System.out.println("Gateway guardado en BD: " + gateway.getId() + 
                          ", tipo: " + gateway.getType() + 
                          ", x: " + gateway.getX() + 
                          ", y: " + gateway.getY());
        
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    // Actualizar Gateway
    public GatewayDTO updateGateway(GatewayDTO gatewayDTO) {
        if (gatewayDTO.getId() == null) {
            throw new IllegalArgumentException("El id del Gateway no puede ser null para actualizar");
        }

        // Verificar que el gateway exista
        Gateway existingGateway = gatewayRepository.findById(gatewayDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + gatewayDTO.getId() + " not found"));


        existingGateway.setType(gatewayDTO.getType());
        existingGateway.setX(gatewayDTO.getX());
        existingGateway.setY(gatewayDTO.getY());

        if (gatewayDTO.getProcessId() != null) {
            Process process = processService.findProcessEntity(gatewayDTO.getProcessId());
            existingGateway.setProcess(process);
        }

        existingGateway = gatewayRepository.save(existingGateway);
        
        System.out.println("Gateway actualizado: " + existingGateway.getId() + 
                          ", x: " + existingGateway.getX() + 
                          ", y: " + existingGateway.getY());
        
        return modelMapper.map(existingGateway, GatewayDTO.class);
    }

    // Buscar Gateway por ID
    public GatewayDTO findGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    public Gateway findGatewayEntity(Long id) {
        return gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));
    }

    // Eliminar Gateway
    public void deleteGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway " + id + " not found"));
        gateway.setStatus("inactive");
        gatewayRepository.save(gateway);
        
        System.out.println("Gateway eliminado (soft delete): " + id);
    }

    // Listar todos los Gateways
    public List<GatewayDTO> findGateways() {
        List<Gateway> gateways = gatewayRepository.findAll();
        
        System.out.println("Gateways en BD: " + gateways.size());
        gateways.forEach(g -> System.out.println("  - ID: " + g.getId() + ", tipo: " + g.getType() + ", x: " + g.getX() + ", y: " + g.getY()));

        return gateways.stream()
                .map(gateway -> modelMapper.map(gateway, GatewayDTO.class))
                .toList();
    }
}