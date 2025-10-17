package com.proyecto.entrega.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proyecto.entrega.dto.*;
import com.proyecto.entrega.entity.*;
import com.proyecto.entrega.entity.Process;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true)
                .setDeepCopyEnabled(false); // ← CRÍTICO

        // ========== COMPANY ==========
        modelMapper.typeMap(Company.class, CompanyDTO.class).addMappings(mapper -> {
            mapper.skip(CompanyDTO::setProcesses);
            mapper.skip(CompanyDTO::setUsers);
        });

        // ========== PROCESS ==========
        modelMapper.typeMap(Process.class, ProcessDTO.class).addMappings(mapper -> {
            mapper.skip(ProcessDTO::setGateways);
            mapper.skip(ProcessDTO::setRoles);
            mapper.skip(ProcessDTO::setEdges);
            mapper.skip(ProcessDTO::setActivities);
            // Mapear company pero SIN sus listas
            mapper.using(companyConverter()).map(Process::getCompany, ProcessDTO::setCompany);
        });

        // ========== ACTIVITY ==========
        modelMapper.typeMap(Activity.class, ActivityDTO.class).addMappings(mapper -> {
            mapper.skip(ActivityDTO::setOutgoingEdges);
            mapper.skip(ActivityDTO::setIncomingEdges);
            // Mapear process pero SIN sus listas
            mapper.using(processConverter()).map(Activity::getProcess, ActivityDTO::setProcess);
        });

        // ========== EDGE ==========
        modelMapper.typeMap(Edge.class, EdgeDTO.class).addMappings(mapper -> {
            // Mapear objetos completos pero SIN ciclos
            mapper.using(processConverter()).map(Edge::getProcess, EdgeDTO::setProcess);
            mapper.using(activityConverter()).map(Edge::getActivitySource, EdgeDTO::setActivitySource);
            mapper.using(activityConverter()).map(Edge::getActivityDestiny, EdgeDTO::setActivityDestiny);
        });

        // ========== GATEWAY ==========
        modelMapper.typeMap(Gateway.class, GatewayDTO.class).addMappings(mapper -> {
            mapper.using(processConverter()).map(Gateway::getProcess, GatewayDTO::setProcess);
        });

        // ========== ROLE ==========
        modelMapper.typeMap(Role.class, RoleDTO.class).addMappings(mapper -> {
            mapper.skip(RoleDTO::setUsers);
            mapper.using(companyConverter()).map(Role::getCompany, RoleDTO::setCompany);
            mapper.using(processConverter()).map(Role::getProcess, RoleDTO::setProcess);
        });

        // ========== USER ==========
        modelMapper.typeMap(User.class, UserDTO.class).addMappings(mapper -> {
            mapper.using(companyConverter()).map(User::getCompany, UserDTO::setCompany);
            mapper.using(roleConverter()).map(User::getRole, UserDTO::setRole);
        });

        modelMapper.typeMap(User.class, UserSafeDTO.class).addMappings(mapper -> {
            mapper.using(companyConverter()).map(User::getCompany, UserSafeDTO::setCompany);
            mapper.using(roleConverter()).map(User::getRole, UserSafeDTO::setRole);
        });

        return modelMapper;
    }

    // ========== CONVERTERS PERSONALIZADOS ==========
    
    private Converter<Company, CompanyDTO> companyConverter() {
        return context -> {
            Company source = context.getSource();
            if (source == null) return null;
            
            CompanyDTO dto = new CompanyDTO();
            dto.setId(source.getId());
            dto.setNit(source.getNit());
            dto.setName(source.getName());
            dto.setCorreoContacto(source.getCorreoContacto());
            dto.setStatus(source.getStatus());
            // NO mapear processes ni users
            return dto;
        };
    }

    private Converter<Process, ProcessDTO> processConverter() {
        return context -> {
            Process source = context.getSource();
            if (source == null) return null;
            
            ProcessDTO dto = new ProcessDTO();
            dto.setId(source.getId());
            dto.setName(source.getName());
            dto.setDescription(source.getDescription());
            dto.setStatus(source.getStatus());
            
            // Mapear company básico (sin listas)
            if (source.getCompany() != null) {
                CompanyDTO companyDTO = new CompanyDTO();
                companyDTO.setId(source.getCompany().getId());
                companyDTO.setNit(source.getCompany().getNit());
                companyDTO.setName(source.getCompany().getName());
                companyDTO.setCorreoContacto(source.getCompany().getCorreoContacto());
                companyDTO.setStatus(source.getCompany().getStatus());
                dto.setCompany(companyDTO);
            }
            // NO mapear gateways, roles, edges, activities
            return dto;
        };
    }

    private Converter<Activity, ActivityDTO> activityConverter() {
        return context -> {
            Activity source = context.getSource();
            if (source == null) return null;
            
            ActivityDTO dto = new ActivityDTO();
            dto.setId(source.getId());
            dto.setName(source.getName());
            dto.setX(source.getX());
            dto.setY(source.getY());
            dto.setDescription(source.getDescription());
            dto.setWidth(source.getWidth());
            dto.setHeight(source.getHeight());
            dto.setStatus(source.getStatus());
            
            // Mapear process básico (sin listas)
            if (source.getProcess() != null) {
                ProcessDTO processDTO = new ProcessDTO();
                processDTO.setId(source.getProcess().getId());
                processDTO.setName(source.getProcess().getName());
                processDTO.setDescription(source.getProcess().getDescription());
                processDTO.setStatus(source.getProcess().getStatus());
                dto.setProcess(processDTO);
            }
            // NO mapear outgoingEdges ni incomingEdges
            return dto;
        };
    }

    private Converter<Role, RoleDTO> roleConverter() {
        return context -> {
            Role source = context.getSource();
            if (source == null) return null;
            
            RoleDTO dto = new RoleDTO();
            dto.setId(source.getId());
            dto.setNombre(source.getNombre());
            dto.setDescripcion(source.getDescripcion());
            dto.setStatus(source.getStatus());
            // NO mapear users, company ni process (evitar ciclos)
            return dto;
        };
    }
}