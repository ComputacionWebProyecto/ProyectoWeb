package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
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

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserService userService;

    // ---------- createUser ----------
    @Test
    void createUser_ok() {
        UserDTO inDto = new UserDTO(null, "Juan", "juan@acme.com", "secreta", 10L, 20L);
        User entityBefore = new User(null, "Juan", "juan@acme.com", "secreta", "active", null, null);
        User entitySaved  = new User(1L,   "Juan", "juan@acme.com", "secreta", "active", null, null);
        UserDTO outDto    = new UserDTO(1L, "Juan", "juan@acme.com", "secreta", 10L, 20L);

        when(modelMapper.map(inDto, User.class)).thenReturn(entityBefore);
        when(userRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, UserDTO.class)).thenReturn(outDto);

        UserDTO result = userService.createUser(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Juan");
        assertThat(result.getCorreo()).isEqualTo("juan@acme.com");
        // createUser retorna UserDTO (incluye contraseña)
        assertThat(result.getContrasena()).isEqualTo("secreta");
        verify(userRepository).save(entityBefore);
    }

    @Test
    void createUser_missingCompany_throwsIAE() {
        UserDTO inDto = new UserDTO(null, "Juan", "juan@acme.com", "secreta", null, 20L);

        assertThatThrownBy(() -> userService.createUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Company ID");

        verifyNoInteractions(userRepository);
    }

    @Test
    void createUser_missingRole_throwsIAE() {
        UserDTO inDto = new UserDTO(null, "Juan", "juan@acme.com", "secreta", 10L, null);

        assertThatThrownBy(() -> userService.createUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role ID");

        verifyNoInteractions(userRepository);
    }

    // ---------- updateUser ----------
    @Test
    void updateUser_ok() {
        UserDTO inDto = new UserDTO(5L, "Juan Edit", "juan.edit@acme.com", "nuevaPass", 10L, 20L);
        User entityExisting = new User(5L, "Juan Viejo", "viejo@acme.com", "old", "active", null, null);
        User entitySaved    = new User(5L, "Juan Edit", "juan.edit@acme.com", "nuevaPass", "active", null, null);
        UserDTO outDto      = new UserDTO(5L, "Juan Edit", "juan.edit@acme.com", "nuevaPass", 10L, 20L);

        when(userRepository.findById(5L)).thenReturn(Optional.of(entityExisting));
        // modelMapper.map(source, destination) es void; no requiere stub (no hace nada por defecto)
        when(userRepository.save(entityExisting)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, UserDTO.class)).thenReturn(outDto);

        UserDTO result = userService.updateUser(inDto);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getNombre()).isEqualTo("Juan Edit");
        assertThat(result.getCorreo()).isEqualTo("juan.edit@acme.com");
        verify(userRepository).findById(5L);
        verify(userRepository).save(entityExisting);
    }

    @Test
    void updateUser_missingId_throwsIAE() {
        UserDTO inDto = new UserDTO(null, "X", "x@x.com", "p", 1L, 2L);

        assertThatThrownBy(() -> userService.updateUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID");

        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUser_missingCompany_throwsIAE() {
        UserDTO inDto = new UserDTO(5L, "X", "x@x.com", "p", null, 2L);

        assertThatThrownBy(() -> userService.updateUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Company ID");

        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUser_missingRole_throwsIAE() {
        UserDTO inDto = new UserDTO(5L, "X", "x@x.com", "p", 1L, null);

        assertThatThrownBy(() -> userService.updateUser(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role ID");

        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUser_notFound_throwsEntityNotFound() {
        UserDTO inDto = new UserDTO(99L, "X", "x@x.com", "p", 1L, 2L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);
    }

    // ---------- findUser ----------
    @Test
    void findUser_found_returnsSafeDTO() {
        User entity = new User(2L, "Ana", "ana@acme.com", "topsecret", "active", null, null);
        UserSafeDTO safe = new UserSafeDTO(2L, "Ana", "ana@acme.com", 11L, 22L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, UserSafeDTO.class)).thenReturn(safe);

        UserSafeDTO result = userService.findUser(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getNombre()).isEqualTo("Ana");
        assertThat(result.getCorreo()).isEqualTo("ana@acme.com");
        // No hay contraseña en UserSafeDTO
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

    // ---------- deleteUser (soft delete) ----------
    @Test
    void deleteUser_softDelete_setsInactiveAndSaves() {
        User entity = new User(7L, "Luis", "luis@acme.com", "pass", "active", null, null);
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

    // ---------- findAllUsers ----------
    @Test
    void findAllUsers_ok() {
        User u = new User(1L, "Pepe", "pepe@acme.com", "p", "active", null, null);
        when(userRepository.findAll()).thenReturn(List.of(u));

        UserSafeDTO safe = new UserSafeDTO(1L, "Pepe", "pepe@acme.com", 50L, 60L);
        when(modelMapper.map(u, UserSafeDTO.class)).thenReturn(safe);

        List<UserSafeDTO> result = userService.findAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Pepe");
        verify(userRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(User.class), eq(UserSafeDTO.class));
    }
}
