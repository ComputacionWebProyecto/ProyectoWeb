package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.UserDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.entity.Company;
import com.proyecto.entrega.entity.Role;
import com.proyecto.entrega.entity.User;
import com.proyecto.entrega.exception.DuplicateResourceException;
import com.proyecto.entrega.exception.ResourceNotFoundException;
import com.proyecto.entrega.exception.ValidationException;
import com.proyecto.entrega.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock CompanyService companyService;
    @Mock RoleService roleService;
    @Mock ModelMapper modelMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    // Helpers
    private Company comp(Long id){ Company c=new Company(); c.setId(id); return c; }
    private Role role(Long id){ Role r=new Role(); r.setId(id); return r; }
    private User user(Long id,String n,String c,String p,String s,Company co,Role ro){
        User u=new User(); u.setId(id); u.setNombre(n); u.setCorreo(c);
        u.setContrasena(p); u.setStatus(s); u.setCompany(co); u.setRole(ro); return u;
    }

    private UserDTO dto(Long id,String n,String c,String p,String s,Long co,Long ro){
        UserDTO d=new UserDTO(); d.setId(id); d.setNombre(n); d.setCorreo(c); d.setContrasena(p);
        d.setStatus(s); d.setCompanyId(co); d.setRoleId(ro); return d;
    }

    private UserSafeDTO safe(Long id,String n,String c,String s,Long co,Long ro){
        UserSafeDTO d=new UserSafeDTO(); d.setId(id); d.setNombre(n); d.setCorreo(c);
        d.setStatus(s); d.setCompanyId(co); d.setRoleId(ro); return d;
    }

    
    @Test
    void createUser_ok() {
        UserDTO inDto = dto(null,"Juan","juan@acme.com","plain",null,10L,20L);

        Company c = comp(10L);
        Role r = role(20L);

        User mapped = user(null,"Juan","juan@acme.com","plain",null,null,null);
        User persisted = user(1L,"Juan","juan@acme.com","ENC",null,c,r);
        UserDTO outDto = dto(1L,"Juan","juan@acme.com","ENC",null,10L,20L);

        when(userRepository.existsByCorreo("juan@acme.com")).thenReturn(false);
        when(modelMapper.map(inDto, User.class)).thenReturn(mapped);
        when(companyService.findCompanyEntity(10L)).thenReturn(c);
        when(roleService.findRoleEntity(20L)).thenReturn(r);
        when(passwordEncoder.encode("plain")).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenReturn(persisted);
        when(modelMapper.map(persisted, UserDTO.class)).thenReturn(outDto);

        UserDTO result = userService.createUser(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContrasena()).isEqualTo("ENC");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User toSave = captor.getValue();
        assertThat(toSave.getContrasena()).isEqualTo("ENC");
        assertThat(toSave.getCompany()).isSameAs(c);
        assertThat(toSave.getRole()).isSameAs(r);
    }

    @Test
    void createUser_nameMissing_error() {
        UserDTO dto = dto(null,"", "a@a.com","p",null,1L,1L);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void createUser_emailMissing_error() {
        UserDTO dto = dto(null,"A", null,"p",null,1L,1L);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("correo");
    }

    @Test
    void createUser_passMissing_error() {
        UserDTO dto = dto(null,"A","a@a.com","",null,1L,1L);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("contraseña");
    }

    @Test
    void createUser_missingCompany_error() {
        UserDTO dto = dto(null,"Juan","a@a.com","p",null,null,1L);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("compañía");
    }

    @Test
    void createUser_missingRole_error() {
        UserDTO dto = dto(null,"Juan","a@a.com","p",null,1L,null);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("rol");
    }

    @Test
    void createUser_duplicateEmail_error() {
        UserDTO dto = dto(null,"Juan","dup@a.com","p",null,1L,1L);

        when(userRepository.existsByCorreo("dup@a.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("correo");
    }

    
    @Test
    void updateUser_ok() {
        UserDTO in = dto(10L,"Nuevo","new@a.com","npass",null,11L,22L);

        User existing = user(10L,"Old","old@a.com","ENC_OLD","active",null,null);
        Company c = comp(11L);
        Role r = role(22L);

        User saved = user(10L,"Nuevo","new@a.com","ENC_NEW","active",c,r);
        UserDTO outDto = dto(10L,"Nuevo","new@a.com","ENC_NEW","active",11L,22L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(companyService.findCompanyEntity(11L)).thenReturn(c);
        when(roleService.findRoleEntity(22L)).thenReturn(r);
        when(passwordEncoder.encode("npass")).thenReturn("ENC_NEW");

        when(userRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, UserDTO.class)).thenReturn(outDto);

        UserDTO result = userService.updateUser(in);

        assertThat(result.getContrasena()).isEqualTo("ENC_NEW");
        assertThat(existing.getCompany()).isSameAs(c);
        assertThat(existing.getRole()).isSameAs(r);
    }

    @Test
    void updateUser_missingId_error() {
        UserDTO dto = dto(null,"A","a@a.com","p",null,1L,1L);
        assertThatThrownBy(() -> userService.updateUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("ID del usuario");
    }

    @Test
    void updateUser_missingCompany_error() {
        UserDTO dto = dto(5L,"A","a@a.com","p",null,null,1L);
        assertThatThrownBy(() -> userService.updateUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("compañía");
    }

    @Test
    void updateUser_missingRole_error() {
        UserDTO dto = dto(5L,"A","a@a.com","p",null,1L,null);
        assertThatThrownBy(() -> userService.updateUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("rol");
    }

    @Test
    void updateUser_notFound_error() {
        UserDTO dto = dto(99L,"A","a@a.com","p",null,1L,1L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    
    @Test
    void findUser_ok() {
        User entity = user(2L,"Ana","ana@a.com","ENC","active",comp(11L),role(22L));
        UserSafeDTO safeDto = safe(2L,"Ana","ana@a.com","active",11L,22L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, UserSafeDTO.class)).thenReturn(safeDto);

        UserSafeDTO out = userService.findUser(2L);

        assertThat(out.getId()).isEqualTo(2L);
        assertThat(out.getCorreo()).isEqualTo("ana@a.com");
    }

    @Test
    void findUser_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    
    @Test
    void deleteUser_ok() {
        User entity = user(7L,"Luis","l@a.com","ENC","active",null,null);
        when(userRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(userRepository.save(entity)).thenReturn(entity);

        userService.deleteUser(7L);

        assertThat(entity.getStatus()).isEqualTo("inactive");
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(123L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("123");
    }

    
    @Test
    void findAllUsers_ok() {
        User u = user(1L,"Pepe","p@a.com","ENC","active",comp(50L),role(60L));
        UserSafeDTO safe = safe(1L,"Pepe","p@a.com","active",50L,60L);

        when(userRepository.findAll()).thenReturn(List.of(u));
        when(modelMapper.map(u, UserSafeDTO.class)).thenReturn(safe);

        List<UserSafeDTO> list = userService.findAllUsers();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getNombre()).isEqualTo("Pepe");
    }
}
