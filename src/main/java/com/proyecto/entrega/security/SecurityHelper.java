package com.proyecto.entrega.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.entrega.dto.CompanyDTO;
import com.proyecto.entrega.dto.RoleDTO;
import com.proyecto.entrega.dto.UserSafeDTO;
import com.proyecto.entrega.exception.UnauthorizedAccessException;

@Component
public class SecurityHelper {

    private final ObjectMapper objectMapper;

    public SecurityHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // =====================================
    // EXTRACT USER (NUEVO MÉTODO CORREGIDO)
    // =====================================
    public UserSafeDTO extractUser(Authentication authentication) {

        System.out.println("\n==== EXTRACT USER (JSON NODE) ====");

        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                throw new UnauthorizedAccessException("Autenticación inválida");
            }

            String jusuarioJson = authentication.getPrincipal().toString();

            System.out.println("Principal JSON: " + jusuarioJson);
            System.out.println("Authorities: " + authentication.getAuthorities());

            JsonNode jsonNode = objectMapper.readTree(jusuarioJson);

            Long userId = jsonNode.get("id").asLong();
            String nombre = jsonNode.get("nombre").asText();
            String correo = jsonNode.get("correo").asText();
            String status = jsonNode.has("status") && !jsonNode.get("status").isNull()
                    ? jsonNode.get("status").asText()
                    : null;

            // ============= COMPANY =============
            CompanyDTO company = null;

            if (jsonNode.has("company") && !jsonNode.get("company").isNull()) {

                JsonNode companyNode = jsonNode.get("company");
                company = new CompanyDTO();

                if (companyNode.has("id")) {
                    company.setId(companyNode.get("id").asLong());
                }
                if (companyNode.has("name")) {
                    company.setName(companyNode.get("name").asText());
                }
                if (companyNode.has("NIT")) {
                    company.setNit(companyNode.get("NIT").asLong());
                }
                if (companyNode.has("correoContacto")) {
                    company.setCorreoContacto(companyNode.get("correoContacto").asText());
                }
                if (companyNode.has("status") && !companyNode.get("status").isNull()) {
                    company.setStatus(companyNode.get("status").asText());
                }

                System.out.println("Company cargada: " + company.getId() + " - " + company.getName());
            } else {
                System.out.println("⚠️ company viene NULL en el token");
            }

            // ============= ROLE =============
            RoleDTO roleObj = null;

            if (jsonNode.has("role") && !jsonNode.get("role").isNull()) {

                JsonNode roleNode = jsonNode.get("role");
                roleObj = new RoleDTO();

                if (roleNode.has("id")) {
                    roleObj.setId(roleNode.get("id").asLong());
                }
                if (roleNode.has("nombre")) {
                    roleObj.setNombre(roleNode.get("nombre").asText());
                }
                if (roleNode.has("descripcion")) {
                    roleObj.setDescripcion(roleNode.get("descripcion").asText());
                }
                if (roleNode.has("status") && !roleNode.get("status").isNull()) {
                    roleObj.setStatus(roleNode.get("status").asText());
                }
                if (roleNode.has("companyId")) {
                    roleObj.setCompanyId(roleNode.get("companyId").asLong());
                }

                System.out.println("Role cargado: " + roleObj.getNombre());
            } else {
                System.out.println("⚠️ role viene NULL en el token");
            }

            // ============= BUILD USER =============
            UserSafeDTO user = new UserSafeDTO();
            user.setId(userId);
            user.setNombre(nombre);
            user.setCorreo(correo);
            user.setStatus(status);
            user.setCompany(company);
            user.setRole(roleObj);

            System.out.println("Usuario final: " + user.getId() + " - " + user.getCorreo());
            System.out.println("=================================\n");

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            throw new UnauthorizedAccessException("Error al procesar usuario");
        }
    }

    // =====================================
    public Long getUserCompanyId(Authentication authentication) {

        UserSafeDTO user = extractUser(authentication);

        if (user.getCompany() == null || user.getCompany().getId() == null) {
            throw new UnauthorizedAccessException("Usuario sin compañía asignada");
        }

        return user.getCompany().getId();
    }

    // =====================================
    public Long getUserId(Authentication authentication) {
        return extractUser(authentication).getId();
    }

    // =====================================
    public String getUserRole(Authentication authentication) {

        UserSafeDTO user = extractUser(authentication);

        if (user.getRole() == null || user.getRole().getNombre() == null) {
            throw new UnauthorizedAccessException("Usuario sin rol asignado");
        }

        return user.getRole().getNombre();
    }

    // =====================================
    public void validateCompanyAccess(Authentication authentication, Long companyId) {

        if (companyId == null)
            return;

        Long userCompanyId = getUserCompanyId(authentication);

        if (!userCompanyId.equals(companyId)) {
            throw new UnauthorizedAccessException(
                    "No tienes permiso para acceder a recursos de otra compañía");
        }
    }

    // =====================================
    public void validateAdminRole(Authentication authentication) {

        String role = getUserRole(authentication);

        if (!"administrador".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException(
                    "Esta acción requiere permisos de administrador");
        }
    }

    // =====================================
    public void validateUserAccess(Authentication authentication, Long userId) {

        if (userId == null)
            return;

        Long authenticatedUserId = getUserId(authentication);

        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedAccessException(
                    "No tienes permiso para acceder a datos de otro usuario");
        }
    }

    // =====================================
    public void validateCompanyResourceAccess(Authentication authentication, Long resourceCompanyId) {

        if (resourceCompanyId == null)
            return;

        Long userCompanyId = getUserCompanyId(authentication);

        if (!userCompanyId.equals(resourceCompanyId)) {
            throw new UnauthorizedAccessException(
                    "Este recurso pertenece a otra compañía");
        }
    }
}
