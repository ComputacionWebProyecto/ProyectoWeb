package com.proyecto.entrega.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.entity.User;
import com.proyecto.entrega.exception.DuplicateResourceException;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO createUser(UserDTO userDTO) {
        // Validación de datos
        if (userDTO.getNombre() == null || userDTO.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre es requerido");
        }
        if (userDTO.getCorreo() == null || userDTO.getCorreo().trim().isEmpty()) {
            throw new ValidationException("El correo electrónico es requerido");
        }
        if (userDTO.getContrasena() == null || userDTO.getContrasena().isBlank()) {
            throw new ValidationException("La contraseña es requerida");
        }

        validateCompanyAndRole(userDTO);

        if (userRepository.existsByCorreo(userDTO.getCorreo())) {
            throw new DuplicateResourceException("Usuario", "correo", userDTO.getCorreo());
        }

        User user = modelMapper.map(userDTO, User.class);

        Company company = companyService.findCompanyEntity(userDTO.getCompanyId());
        Role role = roleService.findRoleEntity(userDTO.getRoleId());

        user.setCompany(company);
        user.setRole(role);

        String contrasenaEncriptada = passwordEncoder.encode(userDTO.getContrasena());
        user.setContrasena(contrasenaEncriptada);

        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new ValidationException("El ID del usuario es requerido para actualizar");
        }
        validateCompanyAndRole(userDTO);

        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userDTO.getId()));

        Company company = companyService.findCompanyEntity(userDTO.getCompanyId());
        Role role = roleService.findRoleEntity(userDTO.getRoleId());

        user.setCompany(company);
        user.setRole(role);
        user.setNombre(userDTO.getNombre());
        user.setCorreo(userDTO.getCorreo());

        if (userDTO.getContrasena() != null && !userDTO.getContrasena().isBlank()) {
            String contrasenaEncriptada = passwordEncoder.encode(userDTO.getContrasena());
            user.setContrasena(contrasenaEncriptada);
        }

        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserSafeDTO findUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return modelMapper.map(user, UserSafeDTO.class);
    }

    public User findUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        user.setStatus("inactive");
        userRepository.save(user);
    }

    public List<UserSafeDTO> getUsersByCompany(Long id, Long excludedUserId) {
        List<User> users = userRepository.findByCompanyIdAndIdNot(id, excludedUserId);
        return users.stream()
                .map(user -> modelMapper.map(user, UserSafeDTO.class))
                .toList();
    }

    public List<UserSafeDTO> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserSafeDTO.class))
                .toList();
    }

    // ========================================
    // MÉTODOS PARA LOGIN
    // ========================================

    public User findByEmailEntity(String correo) {
        User user = userRepository.findByCorreo(correo);
        if (user == null) {
            throw new ResourceNotFoundException("Usuario", "correo", correo);
        }
        return user;
    }

    public UserDTO findByEmail(String correo) {
        User user = userRepository.findByCorreo(correo);
        if (user == null) {
            throw new ResourceNotFoundException("Usuario", "correo", correo);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return modelMapper.map(user, UserDTO.class);
    }

    private void validateCompanyAndRole(UserDTO userDTO) {
        if (userDTO.getCompanyId() == null) {
            throw new ValidationException("El ID de la compañía es requerido");
        }
        if (userDTO.getRoleId() == null) {
            throw new ValidationException("El ID del rol es requerido");
        }
    }
}