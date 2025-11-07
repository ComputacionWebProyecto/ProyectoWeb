package com.proyecto.entrega.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        // configuration.setAllowedOrigins(java.util.List.of("http://localhost:4200", "https://founders.dynaco.co"));
        
        configuration.setAllowedOriginPatterns(java.util.List.of("*"));
        configuration.setAllowedMethods( java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders( java.util.List.of("*"));
        configuration.setAllowCredentials(true);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            // Login endpoint
                .requestMatchers("POST", "/api/auth/auth/login").permitAll()
                /*   
                // Activation endpoint
                .requestMatchers("POST", "/api/auth/user/activate/**").permitAll()
                // App authentication endpoint
                .requestMatchers("POST", "/api/auth/auth/app-authenticate").permitAll()
                // Password recovery endpoint
                .requestMatchers("POST", "/api/auth/user/recover-password").permitAll()
                // Password reset endpoint
                .requestMatchers("POST", "/api/auth/user/reset-password").permitAll()
                // Refresh token endpoint - requiere autenticación
                // .requestMatchers("POST", "/auth/auth/refresh").authenticated()
                // Refresh token endpoint - requiere autenticación
                .requestMatchers("POST", "/api/auth/auth/renew-token").authenticated()
                // Validate token endpoint - requiere autenticación
                .requestMatchers("GET", "/api/auth/auth/validate").authenticated()
                // Endpoints públicos para creación
                .requestMatchers("POST", "/api/auth/user").permitAll()
                .requestMatchers("POST", "/api/auth/enterprise").permitAll()
                // Endpoints GET para usuarios autenticados
                .requestMatchers("GET", "/api/auth/user").authenticated()
                .requestMatchers("GET", "/api/auth/enterprise").authenticated()
                .requestMatchers("GET", "/api/auth/user/{id}").authenticated()
                .requestMatchers("GET", "/auth/enterprise/{id}").authenticated()
                // Endpoints protegidos - requieren ADMIN
                .requestMatchers("PUT", "/api/auth/user/**").authenticated()
                .requestMatchers("PUT", "/api/auth/enterprise/**").authenticated()
                .requestMatchers("DELETE", "/api/auth/user/**").authenticated()
                .requestMatchers("DELETE", "/api/auth/enterprise/**").authenticated()
                .anyRequest().authenticated()*/  
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
