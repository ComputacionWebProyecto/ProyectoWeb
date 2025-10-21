package com.proyecto.entrega.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.entity.User;
import com.proyecto.entrega.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

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

    public UserDTO createUser(UserDTO userDTO) {
        validateCompanyAndRole(userDTO);

        if (userRepository.existsByCorreo(userDTO.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo de usuario ya existe");
        }

        User user = modelMapper.map(userDTO, User.class);

        Company company = companyService.findCompanyEntity(userDTO.getCompanyId());
        Role role = roleService.findRoleEntity(userDTO.getRoleId());

        user.setCompany(company);
        user.setRole(role);

        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new IllegalArgumentException("User ID must not be null for update.");
        }
        validateCompanyAndRole(userDTO);

        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User " + userDTO.getId() + " not found"));

        Company company = companyService.findCompanyEntity(userDTO.getCompanyId());
        Role role = roleService.findRoleEntity(userDTO.getRoleId());

        user.setCompany(company);
        user.setRole(role);
        user.setNombre(userDTO.getNombre());
        user.setCorreo(userDTO.getCorreo());

        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserSafeDTO findUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id + " not found"));
        return modelMapper.map(user, UserSafeDTO.class);
    }

    public User findUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id + " not found"));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id + " not found"));
        user.setStatus("inactive");
        userRepository.save(user);
    }

    public List<UserSafeDTO> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserSafeDTO.class))
                .toList();
    }

    // MÉTODOS PARA LOGIN
    public UserDTO findByEmail(String correo) {
        User user =userRepository.findByCorreo(correo);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id + " not found"));
        return modelMapper.map(user, UserDTO.class);
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