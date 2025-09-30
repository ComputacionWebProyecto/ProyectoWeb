package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.entity.User;
import com.proyecto.entrega.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private UserRepository userRepository;

    // Crear usuario (incluye contraseña)
    public UserDTO createUser(UserDTO userDTO) {
        validateCompanyAndRole(userDTO);
        User user = modelMapper.map(userDTO, User.class);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    // Actualizar usuario (incluye contraseña)
    public UserDTO updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new IllegalArgumentException("User ID must not be null for update.");
        }
        validateCompanyAndRole(userDTO);

        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User " + userDTO.getId() + " not found"));
        
        modelMapper.map(userDTO, user); // Mapea los campos del DTO al entity existente
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    // Buscar usuario por id (solo DTO seguro, sin contraseña)
    public UserSafeDTO findUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id + " not found"));
        return modelMapper.map(user, UserSafeDTO.class);
    }

    // Eliminar usuario (soft delete)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id + " not found"));
        user.setStatus("inactive");
        userRepository.save(user);
    }

    // Listar todos los usuarios (solo DTO seguro)
    public List<UserSafeDTO> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserSafeDTO.class))
                .toList();
    }

    private void validateCompanyAndRole(UserDTO userDTO) {
        if (userDTO.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID must not be null");
        }
        if (userDTO.getRoleId() == null) {
            throw new IllegalArgumentException("Role ID must not be null");
        }
    }
}
