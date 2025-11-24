package com.proyecto.entrega.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.proyecto.entrega.dto.AuthorizedDTO;
import com.proyecto.entrega.dto.LoginDTO;
import com.proyecto.entrega.service.LoginService;


@RestController
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthorizedDTO> login(@RequestBody LoginDTO request)  throws JsonProcessingException{
        AuthorizedDTO response = loginService.authenticate(request);
        return ResponseEntity.ok(response);
    }    
}