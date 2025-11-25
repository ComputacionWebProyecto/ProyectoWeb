package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.RoleRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock RoleRepository roleRepository;
    @Mock CompanyService companyService;
    @Mock ModelMapper modelMapper;

    @InjectMocks RoleService roleService;

    private Company company(Long id) {
        Company c = new Company();
        c.setId(id);
        return c;
    }

    private Role role(Long id, String nombre, String desc, String status, Company comp) {
        Role r = new Role();
        r.setId(id);
        r.setNombre(nombre);
        r.setDescripcion(desc);
        r.setStatus(status);
        r.setCompany(comp);
        return r;
    }

    private RoleDTO dto(Long id, String nombre, String desc, String status, Long companyId) {
        RoleDTO d = new RoleDTO();
        d.setId(id);
        d.setNombre(nombre);
        d.setDescripcion(desc);
        d.setStatus(status);
        d.setCompanyId(companyId);
        return d;
    }

    

    @Test
    void createRole_ok_conCompany() {
        RoleDTO in = dto(null, "Admin", "Rol admin", null, 100L);

        Role mapped = role(null, "Admin", "Rol admin", null, null);
        Company comp = company(100L);
        Role saved = role(1L, "Admin", "Rol admin", null, comp);
        RoleDTO out = dto(1L, "Admin", "Rol admin", null, 100L);

        when(modelMapper.map(in, Role.class)).thenReturn(mapped);
        when(companyService.findCompanyEntity(100L)).thenReturn(comp);
        when(roleRepository.save(any(Role.class))).thenReturn(saved);
        when(modelMapper.map(saved, RoleDTO.class)).thenReturn(out);

        RoleDTO result = roleService.createRole(in);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Admin");
        assertThat(result.getCompanyId()).isEqualTo(100L);

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        Role toSave = captor.getValue();

        assertThat(toSave.getCompany()).isSameAs(comp);
    }

    @Test
    void createRole_companyIdNull_lanzaError() {
        RoleDTO in = dto(null, "X", "Y", null, null);

        assertThatThrownBy(() -> roleService.createRole(in))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("El ID de la compañía es requerido para crear");

        verifyNoInteractions(roleRepository, companyService, modelMapper);
    }

    

    @Test
    void updateRole_ok() {
        RoleDTO in = dto(10L, "Nuevo", "Nueva desc", null, 101L);

        Role existing = role(10L, "Viejo", "Vieja", "active", null);
        Company comp = company(101L);
        Role saved = role(10L, "Nuevo", "Nueva desc", "active", comp);
        RoleDTO out = dto(10L, "Nuevo", "Nueva desc", "active", 101L);

        when(roleRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(companyService.findCompanyEntity(101L)).thenReturn(comp);
        when(roleRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, RoleDTO.class)).thenReturn(out);

        RoleDTO result = roleService.updateRole(in);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(existing.getNombre()).isEqualTo("Nuevo");
        assertThat(existing.getDescripcion()).isEqualTo("Nueva desc");
        assertThat(existing.getCompany()).isSameAs(comp);
    }

    @Test
    void updateRole_idNull_lanzaError() {
        RoleDTO in = dto(null, "X", "Y", null, 1L);

        assertThatThrownBy(() -> roleService.updateRole(in))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("El ID es requerido para actualizar");

        verifyNoInteractions(roleRepository, companyService, modelMapper);
    }

    @Test
    void updateRole_companyIdNull_lanzaError() {
        RoleDTO in = dto(10L, "X", "Y", null, null);

        assertThatThrownBy(() -> roleService.updateRole(in))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("El ID de la compañía es requerido para actualizar");

        verify(roleRepository, never()).findById(anyLong());
    }

    @Test
    void updateRole_noExiste_lanzaEntityNotFound() {
        RoleDTO in = dto(77L, "X", "Y", null, 1L);

        when(roleRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.updateRole(in))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rol")
                .hasMessageContaining("77");

        verify(roleRepository).findById(77L);
        verifyNoInteractions(companyService, modelMapper);
    }

    

    @Test
    void findRole_found() {
        Role entity = role(2L, "Operador", "Ops", "active", null);
        RoleDTO dto = dto(2L, "Operador", "Ops", "active", 100L);

        when(roleRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, RoleDTO.class)).thenReturn(dto);

        RoleDTO out = roleService.findRole(2L);

        assertThat(out.getNombre()).isEqualTo("Operador");
    }

    @Test
    void findRole_notFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findRole(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // =================== DELETE ===================

    @Test
    void deleteRole_ok() {
        Role existing = role(7L, "A", "D", "active", null);

        when(roleRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(roleRepository.save(existing)).thenReturn(existing);

        roleService.deleteRole(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());

        assertThat(captor.getValue().getStatus()).isEqualTo("inactive");
    }

    @Test
    void deleteRole_notFound() {
        when(roleRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.deleteRole(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("404");
    }

    // =================== LIST ===================

    @Test
    void findRoles_ok() {
        Role r1 = role(1L, "A", "da", "active", null);
        Role r2 = role(2L, "B", "db", "active", null);

        when(roleRepository.findAll()).thenReturn(List.of(r1, r2));

        RoleDTO d1 = dto(1L, "A", "da", "active", null);
        RoleDTO d2 = dto(2L, "B", "db", "active", null);

        when(modelMapper.map(r1, RoleDTO.class)).thenReturn(d1);
        when(modelMapper.map(r2, RoleDTO.class)).thenReturn(d2);

        List<RoleDTO> result = roleService.findRoles();

        assertThat(result).hasSize(2);
    }
}
