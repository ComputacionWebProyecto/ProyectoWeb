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
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.service.UserService;

@RestController
@RequestMapping("/api/company")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public void createUser(@RequestBody UserDTO user) {
        userService.createUser(user);
    }

    @PutMapping()
    public void updateUser(@RequestBody UserDTO user) {
        userService.updateUser(user);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping(value = "/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.findUser(id);
    }

    @GetMapping()
    public List<UserDTO> getUser() {
        return userService.findUser();
    }

}
