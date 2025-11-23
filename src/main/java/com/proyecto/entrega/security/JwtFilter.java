package com.proyecto.entrega.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("JwtFilter - Request: " + method + " " + path);
        
        if (path.startsWith("/auth/swagger-ui") || path.equals("/auth/swagger-ui.html") || path.equals("/auth/v3/api-docs") || path.startsWith("/auth/v3/api-docs")  || (path.startsWith("/auth/auth")  && !path.startsWith("/auth/auth/renew-token")) ) {
            System.out.println("JwtFilter - Bypassing JWT check for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // Login
        if (path.equals("/api/login") && method.equals("POST")) {
            System.out.println("JwtFilter - Ruta pública (Login): " + path);
            filterChain.doFilter(request, response); 
            return;
        }
        
        // Registro
        if (path.startsWith("/api/register")) {
            System.out.println("JwtFilter - Ruta pública (Registro): " + path);
            filterChain.doFilter(request, response);  
            return;
        }
        
        // Creación de usuarios
        if (path.equals("/api/user") && method.equals("POST")) {
            System.out.println("JwtFilter - Ruta pública (Crear usuario): " + path);
            filterChain.doFilter(request, response);  
            return;
        }

        System.out.println("JwtFilter - Ruta protegida, validando JWT...");
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JwtFilter - No hay token, acceso denegado");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token JWT requerido\"}");
            return;
        }
        
        String token = authHeader.substring(7);
        
        if (!jwtUtil.validateToken(token)) {
            System.out.println("JwtFilter - Token inválido o expirado");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
            return;
        }
        
        // Token válido, establecer autenticación
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            email, 
            token, 
            java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("JwtFilter - Token válido para: " + email);
        
        // Continuar con la petición
        filterChain.doFilter(request, response);
    }
}
