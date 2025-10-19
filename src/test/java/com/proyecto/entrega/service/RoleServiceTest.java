package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.repository.CompanyRepository;
import com.proyecto.entrega.repository.ProcessRepository;
import com.proyecto.entrega.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock RoleRepository roleRepository;
    @Mock CompanyRepository companyRepository;
    @Mock ProcessRepository processRepository;
    @Mock ModelMapper modelMapper;

    @InjectMocks RoleService roleService;

    // -------- create --------
    @Test
    void createRole_ok() {
        RoleDTO in = new RoleDTO(null, "Admin", "Rol admin", 100L, 200L);

        Role mapped = new Role();
        mapped.setNombre("Admin");
        mapped.setDescripcion("Rol admin");

        Company company = new Company();
        company.setId(100L);

        Process process = new Process();
        process.setId(200L);

        Role saved = new Role();
        saved.setId(1L);
        saved.setNombre("Admin");
        saved.setDescripcion("Rol admin");
        saved.setCompany(company);
        saved.setProcess(process);

        RoleDTO out = new RoleDTO(1L, "Admin", "Rol admin", 100L, 200L);

        when(modelMapper.map(in, Role.class)).thenReturn(mapped);
        when(companyRepository.findById(100L)).thenReturn(Optional.of(company));
        when(processRepository.findById(200L)).thenReturn(Optional.of(process));
        when(roleRepository.save(mapped)).thenReturn(saved);
        when(modelMapper.map(saved, RoleDTO.class)).thenReturn(out);

        RoleDTO result = roleService.createRole(in);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Admin");
        verify(roleRepository).save(mapped);
    }

    // -------- update --------
    @Test
    void updateRole_ok() {
        RoleDTO in = new RoleDTO(10L, "NuevoNombre", "NuevaDesc", 101L, 202L);

        Role existing = new Role();
        existing.setId(10L);
        existing.setNombre("Viejo");
        existing.setDescripcion("Vieja");

        Company company = new Company();
        company.setId(101L);

        Process process = new Process();
        process.setId(202L);

        Role saved = new Role();
        saved.setId(10L);
        saved.setNombre("NuevoNombre");
        saved.setDescripcion("NuevaDesc");
        saved.setCompany(company);
        saved.setProcess(process);

        RoleDTO out = new RoleDTO(10L, "NuevoNombre", "NuevaDesc", 101L, 202L);

        // <- FALTABA en tu test: el findById usado por el service
        when(roleRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(companyRepository.findById(101L)).thenReturn(Optional.of(company));
        when(processRepository.findById(202L)).thenReturn(Optional.of(process));
        when(roleRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, RoleDTO.class)).thenReturn(out);

        RoleDTO result = roleService.updateRole(in);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNombre()).isEqualTo("NuevoNombre");
        assertThat(result.getDescripcion()).isEqualTo("NuevaDesc");

        verify(roleRepository).findById(10L);
        verify(roleRepository).save(existing);
        verify(modelMapper).map(saved, RoleDTO.class);
    }

    // -------- find uno --------
    @Test
    void findRole_found() {
        Role entity = new Role();
        entity.setId(2L);
        entity.setNombre("Operador");
        entity.setDescripcion("Ops");

        RoleDTO dto = new RoleDTO(2L, "Operador", "Ops", null, null);

        when(roleRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, RoleDTO.class)).thenReturn(dto);

        RoleDTO result = roleService.findRole(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getNombre()).isEqualTo("Operador");
        verify(roleRepository).findById(2L);
    }

    @Test
    void findRole_notFound_throws() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findRole(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(roleRepository).findById(99L);
    }

    // -------- delete (soft delete) --------
    @Test
    void deleteRole_ok() {
        Role existing = new Role();
        existing.setId(7L);
        existing.setStatus("active");

        when(roleRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

        roleService.deleteRole(7L);

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        Role saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(7L);
        assertThat(saved.getStatus()).isEqualTo("inactive");
    }

    // -------- list --------
    @Test
    void findRoles_ok() {
        Role r = new Role();
        r.setId(1L);
        r.setNombre("A");

        when(roleRepository.findAll()).thenReturn(List.of(r));
        when(modelMapper.map(r, RoleDTO.class)).thenReturn(new RoleDTO(1L, "A", null, null, null));

        List<RoleDTO> list = roleService.findRoles();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(1L);
        verify(roleRepository).findAll();
    }
}
