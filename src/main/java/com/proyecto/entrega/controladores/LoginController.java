package com.proyecto.entrega.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.entrega.dto.LoginDTO;
import com.proyecto.entrega.service.LoginService;
import com.proyecto.entrega.dto.UserDTO;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginDTO request) {
        UserDTO response = loginService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}