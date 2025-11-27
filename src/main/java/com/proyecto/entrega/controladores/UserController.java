package com.proyecto.entrega.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.service.UserService;
import com.proyecto.entrega.exception.UnauthorizedAccessException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('administrador')")
    @PostMapping()
    public UserDTO createUser(Authentication authentication, @RequestBody UserDTO user) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return userService.createUser(user);
    }

    @PreAuthorize("hasRole('administrador')")
    @PutMapping()
    public UserDTO updateUser(Authentication authentication, @RequestBody UserDTO user) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return userService.updateUser(user);
    }

    @PreAuthorize("hasRole('administrador')")
    @DeleteMapping(value = "/{id}")
    public void deleteUser(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        userService.deleteUser(id);
    }

    @GetMapping(value = "/{id}")
    public UserSafeDTO getUser(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return userService.findUser(id);
    }

    @GetMapping()
    public List<UserSafeDTO> getUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return userService.findAllUsers();
    }

    
    @GetMapping(value = "/company/{id}/currentUser")
    public List<UserSafeDTO> getUsersByCompany(Authentication authentication, @PathVariable Long id,
            @RequestParam Long currentUserId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return userService.getUsersByCompany(id, currentUserId);
    }

}
