package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.entity.User;
import com.proyecto.entrega.repository.UserRepository;
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
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserService userService;

    @Test
    void createUser_ok() {
        UserDTO inDto = new UserDTO(null, "Juan", "juan@co.com", "secreta"); // ajusta campos si tu DTO difiere
        User entityBefore = new User(null, "Juan", "juan@co.com", "secreta", null, null);
        User entitySaved  = new User(1L,   "Juan", "juan@co.com", "secreta", null, null);
        UserDTO outDto    = new UserDTO(1L, "Juan", "juan@co.com", "secreta");

        when(modelMapper.map(inDto, User.class)).thenReturn(entityBefore);
        when(userRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, UserDTO.class)).thenReturn(outDto);

        UserDTO result = userService.createUser(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Juan");
        verify(userRepository).save(entityBefore);
    }

    @Test
    void updateUser_ok() {
        UserDTO inDto = new UserDTO(10L, "Editado", "e@co.com", "nueva");
        User entity = new User(10L, "Editado", "e@co.com", "nueva", null, null);

        when(modelMapper.map(inDto, User.class)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, UserDTO.class)).thenReturn(inDto);

        UserDTO result = userService.updateUser(inDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Editado");
        verify(userRepository).save(entity);
    }

    @Test
    void findUser_found() {
        User entity = new User(2L, "Ana", "a@co.com", "pwd", null, null);
        UserDTO dto = new UserDTO(2L, "Ana", "a@co.com", "pwd");

        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, UserDTO.class)).thenReturn(dto);

        UserDTO result = userService.findUser(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Ana");
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

    @Test
    void deleteUser_ok() {
        userService.deleteUser(7L);
        verify(userRepository).deleteById(7L);
    }

    @Test
    void findUser_list_ok() {
        User a = new User(1L, "Lu", "l@co.com", "pwd", null, null);
        when(userRepository.findAll()).thenReturn(List.of(a));

        UserDTO dto = new UserDTO(1L, "Lu", "l@co.com", "pwd");
        when(modelMapper.map(a, UserDTO.class)).thenReturn(dto);

        List<UserDTO> result = userService.findUser();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Lu");
        verify(userRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(User.class), eq(UserDTO.class));
    }
}

