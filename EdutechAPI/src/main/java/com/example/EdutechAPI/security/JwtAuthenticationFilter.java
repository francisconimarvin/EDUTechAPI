package com.example.EdutechAPI.security;

import com.example.EdutechAPI.config.AESUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AESUtil aesUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(AESUtil aesUtil, UserDetailsServiceImpl userDetailsService) {
        this.aesUtil = aesUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener el token JWT del encabezado Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail; // Será el username

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si no hay token o no empieza con "Bearer ", pasamos al siguiente filtro
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Extraemos el token (después de "Bearer ")

        // 2. Extraer el email del token
        userEmail = aesUtil.extractUsername(jwt);

        // 3. Validar el token y autenticar al usuario
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (aesUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // Contraseña es null porque ya estamos autenticados por el token
                    userDetails.getAuthorities() // Roles del usuario
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecemos el usuario en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Pasamos la solicitud al siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}