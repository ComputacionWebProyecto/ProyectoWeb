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

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping()
    public void createCompany(@RequestBody CompanyDTO company) {
        companyService.createCompany(company);
    }

    @PutMapping()
    public void updateCompany(@RequestBody CompanyDTO company) {
        companyService.updateCompany(company);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }

    @GetMapping(value = "/{id}")
    public CompanyDTO getCompany(@PathVariable Long id) {
        return companyService.findCompany(id);
    }

    @GetMapping()
    public List<CompanyDTO> getCompany() {
        return companyService.findCompany();
    }

}
