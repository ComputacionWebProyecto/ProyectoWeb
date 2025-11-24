package com.proyecto.entrega.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.proyecto.entrega.dto.AuthorizedDTO;
import com.proyecto.entrega.security.JwtUtil;

/**
 * Controlador para operaciones de autenticación y gestión de tokens JWT.
 * Maneja renovación de tokens y obtención de información del usuario autenticado.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/renew-token")
    public ResponseEntity<AuthorizedDTO> renewToken(Authentication authentication) {
        try {
            AuthorizedDTO response = jwtUtil.renewToken(authentication);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }
}