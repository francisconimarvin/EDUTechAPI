package com.example.EdutechAPI.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs REST con JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura la política de sesión sin estado
            .authorizeHttpRequests(authorize -> authorize
                // Rutas públicas para autenticación
                .requestMatchers("/auth/login").permitAll()
        

                // Rutas públicas para Swagger UI y OpenAPI docs
                // Para .HTML se puede utilizar el navegador directamente
                .requestMatchers(
                    "/swagger-ui/**",          // Para el Swagger UI HTML y todos sus recursos (CSS, JS, etc.)
                    "/v3/api-docs/**",         // Para la especificación OpenAPI en JSON/YAML
                    "/swagger-resources/**",   // Recursos adicionales de Swagger
                    "/swagger-ui.html",        // El archivo HTML principal de Swagger UI 
                    "/webjars/**"              // Recursos de WebJars 
                ).permitAll()

                // Todas las demás solicitudes requieren autenticación
                .anyRequest().authenticated()
            );

        // Añadimos nuestro filtro JWT antes del filtro de autenticación de nombre de usuario/contraseña
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        

        return http.build();
    }
}