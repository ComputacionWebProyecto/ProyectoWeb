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
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("JwtFilter - Request: " + method + " " + path);

        // ========================================
        // 1. RUTAS PÚBLICAS (sin JWT)
        // ========================================
        if (isPublicRoute(path, method)) {
            System.out.println("JwtFilter - Ruta pública: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // ========================================
        // 2. OBTENER TOKEN DEL HEADER
        // ========================================
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JwtFilter - Token ausente");
            sendUnauthorizedResponse(response, "Token JWT requerido");
            return;
        }

        String token = authHeader.substring(7);

        // ========================================
        // 3. RENOVACIÓN DE TOKEN (CASO ESPECIAL)
        // ========================================
        if (path.equals("/api/auth/renew-token") && method.equals("POST")) {
            System.out.println("JwtFilter - Ruta de renovación detectada");
            handleTokenRenewal(token, request, response, filterChain);
            return;  
        }

        // ========================================
        // 4. RUTAS PROTEGIDAS (validar token estrictamente)
        // ========================================
        System.out.println("JwtFilter - Ruta protegida, validando JWT");

        if (!jwtUtil.validateToken(token)) {
            System.out.println("JwtFilter - Token inválido o expirado");
            sendUnauthorizedResponse(response, "Token inválido o expirado");
            return;
        }

        // Token válido -> autenticar
        authenticateAndContinue(token, request, response, filterChain);
    }

    /**
     * Maneja renovación de token - PERMITE TOKENS EXPIRADOS
     */
    private void handleTokenRenewal(
            String token,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        try {
            // Intentar validar el token normalmente
            if (jwtUtil.validateToken(token)) {
                // Token aún válido -> permitir renovación de todos modos
                System.out.println("JwtFilter - Token válido, permitiendo renovación");
                authenticateAndContinue(token, request, response, filterChain);
                return;
            }
            System.out.println("JwtFilter - Token inválido, intentando parsear como expirado");
            
            // Forzar parsing que lanzará ExpiredJwtException si está expirado
            try {
                jwtUtil.extractEmail(token); // Esto lanzará la excepción
                // Si no lanza excepción, el token es inválido por otra razón
                System.out.println("JwtFilter - Token inválido (firma incorrecta)");
                sendUnauthorizedResponse(response, "Token inválido");
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // TOKEN EXPIRADO -> PERMITIR RENOVACIÓN
                System.out.println("JwtFilter - Token expirado, permitiendo renovación");
                
                String email = e.getClaims().getSubject();
                String role = (String) e.getClaims().get("role");

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        token,
                        java.util.Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + role)));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("JwtFilter - Token expirado capturado, permitiendo renovación");

            String email = e.getClaims().getSubject();
            String role = (String) e.getClaims().get("role");

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    token,
                    java.util.Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + role)));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.err.println("JwtFilter - Error inesperado: " + e.getMessage());
            e.printStackTrace();
            sendUnauthorizedResponse(response, "Error procesando token");
        }
    }

    /**
     * Autentica y continúa con la petición
     */
    private void authenticateAndContinue(
            String token,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                email,
                token,
                java.util.Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role)));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("JwtFilter - Autenticación establecida para: " + email);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(String path, String method) {
        return (path.equals("/api/login") && method.equals("POST")) ||
                (path.equals("/api/register") && method.equals("POST")) ||
                path.startsWith("/auth/swagger-ui") ||
                path.startsWith("/auth/v3/api-docs");
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorJson = String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}",
                java.time.LocalDateTime.now().toString(),
                message);

        response.getWriter().write(errorJson);
    }
}