package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.entity.User;
import com.proyecto.entrega.repository.UserRepository;
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
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock CompanyService companyService; // ← carga la empresa por id
    @Mock RoleService roleService;       // ← carga el rol por id
    @Mock ModelMapper modelMapper;

    @InjectMocks UserService userService;

    // ---------- helpers ----------
    private Company comp(Long id){ Company c=new Company(); c.setId(id); return c; }
    private Role role(Long id){ Role r=new Role(); r.setId(id); return r; }

    private User user(Long id, String nombre, String correo, String pass, String status, Company c, Role r){
        User u = new User();
        u.setId(id); u.setNombre(nombre); u.setCorreo(correo); u.setContrasena(pass); u.setStatus(status);
        u.setCompany(c); u.setRole(r);
        return u;
    }

    private UserDTO dto(Long id, String nombre, String correo, String pass, String status, Long companyId, Long roleId){
        UserDTO d = new UserDTO();
        d.setId(id); d.setNombre(nombre); d.setCorreo(correo); d.setContrasena(pass); d.setStatus(status);
        d.setCompanyId(companyId); d.setRoleId(roleId);
        return d;
    }

    private UserSafeDTO safe(Long id, String nombre, String correo, String status, Long companyId, Long roleId){
        UserSafeDTO s = new UserSafeDTO();
        s.setId(id); s.setNombre(nombre); s.setCorreo(correo); s.setStatus(status);
        s.setCompanyId(companyId); s.setRoleId(roleId);
        return s;
    }

    // ---------- createUser ----------
    @Test
    void createUser_ok_statusPorDefectoActive_y_relacionesSeteadas() {
        UserDTO inDto = dto(null, "Juan", "juan@acme.com", "secreta", null, 10L, 20L);

        Company c = comp(10L);
        Role r = role(20L);

        User mapped   = user(null, "Juan", "juan@acme.com", "secreta", null, null, null); // mapper pone lo básico
        User persisted= user(1L, "Juan", "juan@acme.com", "secreta", "active", c, r);
        UserDTO outDto= dto(1L, "Juan", "juan@acme.com", "secreta", "active", 10L, 20L);

        when(modelMapper.map(inDto, User.class)).thenReturn(mapped);
        when(companyService.findCompanyEntity(10L)).thenReturn(c);
        when(roleService.findRoleEntity(20L)).thenReturn(r);
        when(userRepository.save(any(User.class))).thenReturn(persisted);
        when(modelMapper.map(persisted, UserDTO.class)).thenReturn(outDto);

        UserDTO result = userService.createUser(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("active");
        assertThat(result.getContrasena()).isEqualTo("secreta"); // create devuelve DTO completo

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();
        assertThat(toSave.getStatus()).isEqualTo("active");
        assertThat(toSave.getCompany()).isSameAs(c);
        assertThat(toSave.getRole()).isSameAs(r);
    }

    @Test
    void createUser_missingCompany_throwsIAE() {
        UserDTO inDto = dto(null, "Juan", "juan@acme.com", "secreta", null, null, 20L);

        assertThatThrownBy(() -> userService.createUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Company ID");

        verifyNoInteractions(userRepository, companyService, roleService, modelMapper);
    }

    @Test
    void createUser_missingRole_throwsIAE() {
        UserDTO inDto = dto(null, "Juan", "juan@acme.com", "secreta", null, 10L, null);

        assertThatThrownBy(() -> userService.createUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role ID");

        verifyNoInteractions(userRepository, companyService, roleService, modelMapper);
    }

    // ---------- updateUser ----------
    @Test
    void updateUser_ok_actualizaDatos_y_relaciones() {
        UserDTO inDto = dto(10L, "Nuevo Nombre", "nuevo@correo.com", "secreta", null, 11L, 22L);

        User existing = user(10L, "Viejo", "viejo@correo.com", "old", "active", null, null);
        Company c = comp(11L);
        Role r = role(22L);
        User saved = user(10L, "Nuevo Nombre", "nuevo@correo.com", "secreta", "active", c, r);
        UserDTO outDto = dto(10L, "Nuevo Nombre", "nuevo@correo.com", "secreta", "active", 11L, 22L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(companyService.findCompanyEntity(11L)).thenReturn(c);
        when(roleService.findRoleEntity(22L)).thenReturn(r);

        // ModelMapper.map(sourceDTO, destinationEntity) (void)
        doAnswer(inv -> {
            UserDTO src = inv.getArgument(0);
            User dest  = inv.getArgument(1);
            dest.setNombre(src.getNombre());
            dest.setCorreo(src.getCorreo());
            dest.setContrasena(src.getContrasena());
            return null;
        }).when(modelMapper).map(eq(inDto), same(existing));

        when(userRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, UserDTO.class)).thenReturn(outDto);

        UserDTO result = userService.updateUser(inDto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(existing.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(existing.getCorreo()).isEqualTo("nuevo@correo.com");
        assertThat(existing.getContrasena()).isEqualTo("secreta");
        assertThat(existing.getCompany()).isSameAs(c);
        assertThat(existing.getRole()).isSameAs(r);

        verify(modelMapper).map(eq(inDto), same(existing));
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_missingId_throwsIAE() {
        UserDTO inDto = dto(null, "X", "x@x.com", "p", null, 1L, 2L);

        assertThatThrownBy(() -> userService.updateUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID");

        verifyNoInteractions(userRepository, companyService, roleService, modelMapper);
    }

    @Test
    void updateUser_missingCompany_throwsIAE() {
        UserDTO inDto = dto(5L, "X", "x@x.com", "p", null, null, 2L);

        assertThatThrownBy(() -> userService.updateUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Company ID");

        verifyNoInteractions(companyService, roleService);
    }

    @Test
    void updateUser_missingRole_throwsIAE() {
        UserDTO inDto = dto(5L, "X", "x@x.com", "p", null, 1L, null);

        assertThatThrownBy(() -> userService.updateUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role ID");

        verifyNoInteractions(roleService);
    }

    @Test
    void updateUser_notFound_throwsEntityNotFound() {
        UserDTO inDto = dto(99L, "X", "x@x.com", "p", null, 1L, 2L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);
    }

    // ---------- findUser (SAFE) ----------
    @Test
    void findUser_found_returnsSafeDTO_sinContrasena() {
        User entity = user(2L, "Ana", "ana@acme.com", "topsecret", "active", comp(11L), role(22L));
        UserSafeDTO safe = safe(2L, "Ana", "ana@acme.com", "active", 11L, 22L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, UserSafeDTO.class)).thenReturn(safe);

        UserSafeDTO result = userService.findUser(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getNombre()).isEqualTo("Ana");
        assertThat(result.getCorreo()).isEqualTo("ana@acme.com");
        // No hay contraseña en el SAFE DTO
        verify(userRepository).findById(2L);
    }

    @Test
    void findUser_notFound_throwsEntityNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUser(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(userRepository).findById(999L);
        verifyNoMoreInteractions(userRepository);
    }

    // ---------- deleteUser (soft) ----------
    @Test
    void deleteUser_softDelete_setsInactiveAndSaves() {
        User entity = user(7L, "Luis", "luis@acme.com", "pass", "active", null, null);
        when(userRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(userRepository.save(any(User.class))).thenReturn(entity);

        userService.deleteUser(7L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(7L);
        assertThat(saved.getStatus()).isEqualTo("inactive");
    }

    @Test
    void deleteUser_notFound_throwsEntityNotFound() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(123L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("123");

        verify(userRepository).findById(123L);
        verifyNoMoreInteractions(userRepository);
    }

    // ---------- findAllUsers (SAFE) ----------
    @Test
    void findAllUsers_ok_returnsSafeDTOs() {
        User u = user(1L, "Pepe", "pepe@acme.com", "p", "active", comp(50L), role(60L));
        when(userRepository.findAll()).thenReturn(List.of(u));

        UserSafeDTO safe = safe(1L, "Pepe", "pepe@acme.com", "active", 50L, 60L);
        when(modelMapper.map(u, UserSafeDTO.class)).thenReturn(safe);

        List<UserSafeDTO> result = userService.findAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Pepe");
        assertThat(result.get(0).getStatus()).isEqualTo("active");
        verify(userRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(User.class), eq(UserSafeDTO.class));
    }
}
