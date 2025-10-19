package com.proyecto.entrega.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.entrega.dto.RegistrationDTO;
import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.service.RegistrationService;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> register(@RequestBody RegistrationDTO request) {
        UserDTO createdUser = registrationService.registerCompanyAndAdmin(request.getCompany(), request.getUser());
        return ResponseEntity.ok(createdUser);
    }
}

