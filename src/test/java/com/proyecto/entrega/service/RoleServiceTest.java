package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    RoleRepository roleRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    RoleService roleService;

    @Test
    void createRole_ok() {
        RoleDTO inDto = new RoleDTO(); // rellena si tienes más campos
        Role entityBefore = new Role();
        Role entitySaved  = new Role();
        RoleDTO outDto    = new RoleDTO();
        // supongamos que el guardado asigna id=1
        outDto.setId(1L);

        when(modelMapper.map(inDto, Role.class)).thenReturn(entityBefore);
        when(roleRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, RoleDTO.class)).thenReturn(outDto);

        RoleDTO result = roleService.createRole(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        verify(roleRepository).save(entityBefore);
    }

    @Test
    void updateRole_ok() {
        RoleDTO inDto = new RoleDTO();
        inDto.setId(10L);

        Role entity = new Role();

        when(modelMapper.map(inDto, Role.class)).thenReturn(entity);
        when(roleRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, RoleDTO.class)).thenReturn(inDto);

        RoleDTO result = roleService.updateRole(inDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(roleRepository).save(entity);
    }

    @Test
    void findRole_found() {
        Role entity = new Role();
        RoleDTO dto = new RoleDTO();
        dto.setId(2L);

        when(roleRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, RoleDTO.class)).thenReturn(dto);

        RoleDTO result = roleService.findRole(2L);

        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    void findRole_notFound_throwsEntityNotFound() {
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findRole(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(roleRepository).findById(999L);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void deleteRole_ok() {
        roleService.deleteRole(7L);
        verify(roleRepository).deleteById(7L);
    }

    @Test
    void findRoles_ok() {
        Role a = new Role();
        when(roleRepository.findAll()).thenReturn(List.of(a));

        RoleDTO dto = new RoleDTO();
        dto.setId(1L);
        when(modelMapper.map(a, RoleDTO.class)).thenReturn(dto);

        List<RoleDTO> result = roleService.findRoles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(roleRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Role.class), eq(RoleDTO.class));
    }
}
