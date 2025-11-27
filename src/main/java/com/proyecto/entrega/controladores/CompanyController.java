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

import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.service.CompanyService;
import com.proyecto.entrega.exception.UnauthorizedAccessException;
import com.proyecto.entrega.security.SecurityHelper;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SecurityHelper securityHelper;

    @PostMapping()
    public CompanyDTO createCompany(@RequestBody CompanyDTO company) {
        return companyService.createCompany(company);
    }

    @PutMapping()
    public CompanyDTO updateCompany(Authentication authentication, @RequestBody CompanyDTO company) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return companyService.updateCompany(company);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCompany(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        companyService.deleteCompany(id);
    }

    @GetMapping(value = "/{id}")
    public CompanyDTO getCompany(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        securityHelper.validateCompanyAccess(authentication, id);
        return companyService.findCompany(id);
    }

    @GetMapping()
    public List<CompanyDTO> getCompany(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Usuario no autenticado");
        }
        return companyService.findCompanies();
    }

}
