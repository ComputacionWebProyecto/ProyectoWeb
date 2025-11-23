package com.proyecto.entrega.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.proyecto.entrega.dto.AuthorizedDTO;
import com.proyecto.entrega.dto.RegistrationDTO;
import com.proyecto.entrega.service.RegistrationService;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<AuthorizedDTO> register(@RequestBody RegistrationDTO request) {
        try{
            AuthorizedDTO response = registrationService.registerCompanyAndAdmin(request.getCompany(), request.getUser());
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e){
            return ResponseEntity.status(500).build();
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }
}

