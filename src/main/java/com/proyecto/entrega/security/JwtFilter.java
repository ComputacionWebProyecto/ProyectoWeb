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

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final int MAX_DAYS_TO_RENEW_EXPIRED_TOKEN = 7;

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

        try {
            // Validar token
            if (!jwtUtil.validateToken(token)) {
                System.out.println("JwtFilter - Token inválido o expirado");
                sendUnauthorizedResponse(response, "Token inválido o expirado");
                return;
            }

            // Token válido -> autenticar
            authenticateAndContinue(token, request, response, filterChain);

        } catch (ExpiredJwtException e) {
            System.out.println("JwtFilter - Token expirado en ruta protegida");
            sendUnauthorizedResponse(response, "Token expirado, por favor renueva tu sesión");

        } catch (SignatureException e) {
            System.out.println("JwtFilter - Firma inválida: " + e.getMessage());
            sendUnauthorizedResponse(response, "Token con firma inválida");

        } catch (MalformedJwtException e) {
            System.out.println("JwtFilter - Token malformado: " + e.getMessage());
            sendUnauthorizedResponse(response, "Token malformado");

        } catch (Exception e) {
            System.err.println("JwtFilter - Error inesperado: " + e.getMessage());
            e.printStackTrace();
            sendUnauthorizedResponse(response, "Error al validar token");
        }
    }

    private void handleTokenRenewal(
            String token,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        try {
            // Intentar validar el token normalmente
            if (jwtUtil.validateToken(token)) {
                // Token aún válido -> permitir renovación
                System.out.println("JwtFilter - Token válido, permitiendo renovación");
                authenticateAndContinue(token, request, response, filterChain);
                return;
            }
            
            // Si llegamos aquí, el token NO es válido
            System.out.println("JwtFilter - Token no válido, verificando si solo está expirado");
            
            // Intentar extraer claims - esto lanzará ExpiredJwtException si está expirado
            try {
                jwtUtil.extractEmail(token);
                
                // Si llegamos aquí, el token es inválido por otra razón (no solo expirado)
                System.out.println("JwtFilter - Token inválido (firma incorrecta u otro error)");
                sendUnauthorizedResponse(response, "Token inválido, no se puede renovar");
                
            } catch (ExpiredJwtException expiredException) {
                // TOKEN EXPIRADO CON FIRMA VÁLIDA
                System.out.println("JwtFilter - Token expirado, validando ventana de renovación");
                
                // NUEVA VALIDACIÓN: Verificar que no haya expirado hace mucho tiempo
                long expirationTime = expiredException.getClaims().getExpiration().getTime();
                long now = System.currentTimeMillis();
                long daysSinceExpiration = (now - expirationTime) / (1000 * 60 * 60 * 24);
                
                if (daysSinceExpiration > MAX_DAYS_TO_RENEW_EXPIRED_TOKEN) {
                    System.out.println("JwtFilter - Token expiró hace " + daysSinceExpiration + 
                        " días (límite: " + MAX_DAYS_TO_RENEW_EXPIRED_TOKEN + " días)");
                    sendUnauthorizedResponse(response, 
                        "Token expirado hace demasiado tiempo, por favor inicia sesión nuevamente");
                    return;
                }
                
                // TOKEN EXPIRADO RECIENTEMENTE -> PERMITIR RENOVACIÓN
                System.out.println("JwtFilter - Token expiró hace " + daysSinceExpiration + 
                    " días, permitiendo renovación");
                
                String email = expiredException.getClaims().getSubject();
                String role = (String) expiredException.getClaims().get("role");

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        token,
                        java.util.Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + role)));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }

        } catch (ExpiredJwtException e) {
            // Captura adicional por si extractEmail no lo captura
            System.out.println("JwtFilter - Token expirado (captura secundaria)");
            
            // Validar ventana de renovación
            long expirationTime = e.getClaims().getExpiration().getTime();
            long now = System.currentTimeMillis();
            long daysSinceExpiration = (now - expirationTime) / (1000 * 60 * 60 * 24);
            
            if (daysSinceExpiration > MAX_DAYS_TO_RENEW_EXPIRED_TOKEN) {
                System.out.println("JwtFilter - Token expirado hace demasiado tiempo");
                sendUnauthorizedResponse(response, 
                    "Token expirado hace demasiado tiempo, por favor inicia sesión nuevamente");
                return;
            }

            String email = e.getClaims().getSubject();
            String role = (String) e.getClaims().get("role");

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    token,
                    java.util.Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + role)));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (SignatureException e) {
            // Firma inválida - NO PERMITIR renovación
            System.err.println("JwtFilter - Firma inválida en renovación: " + e.getMessage());
            sendUnauthorizedResponse(response, "Token con firma inválida, no se puede renovar");

        } catch (MalformedJwtException e) {
            System.err.println("JwtFilter - Token malformado: " + e.getMessage());
            sendUnauthorizedResponse(response, "Token malformado");

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

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    token,
                    java.util.Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + role)));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("JwtFilter - Autenticación establecida para: " + email + " con rol: " + role);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.err.println("JwtFilter - Error al establecer autenticación: " + e.getMessage());
            sendUnauthorizedResponse(response, "Error al procesar autenticación");
        }
    }

    private boolean isPublicRoute(String path, String method) {
        return (path.equals("/api/login") && method.equals("POST")) ||
                (path.equals("/api/register") && method.equals("POST")) ||
                path.startsWith("/auth/swagger-ui") ||
                path.startsWith("/auth/v3/api-docs") ||
                path.equals("/h2-console") ||
                path.startsWith("/h2-console/");
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorJson = String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}",
                LocalDateTime.now().toString(),
                message);

        response.getWriter().write(errorJson);
    }
}