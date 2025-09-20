package com.example.EdutechAPI.security;

import com.example.EdutechAPI.api.usuarios.model.Rol;
import com.example.EdutechAPI.api.usuarios.model.Usuario;
import com.example.EdutechAPI.api.usuarios.repository.UsuarioRepository;
import com.example.EdutechAPI.config.AESUtil; 
import com.example.EdutechAPI.security.dto.JwtResponse;
import com.example.EdutechAPI.security.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder; // Necesario si vas a registrar usuarios
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth") 
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AESUtil aesUtil; 
    private final UserDetailsServiceImpl userDetailsService; // Nuestro servicio de detalles de usuario
    private final UsuarioRepository usuarioRepository; // Para obtener datos del usuario
    private final PasswordEncoder passwordEncoder; // Solo para usuarios nuevos

    public AuthController(AuthenticationManager authenticationManager, AESUtil aesUtil,
                          UserDetailsServiceImpl userDetailsService, UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.aesUtil = aesUtil;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Autenticar las credenciales del usuario usando Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Si la autenticación fue exitosa, establecerla en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Obtener los detalles del usuario autenticado
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 3. Generar el token JWT
            String jwt = aesUtil.generateToken(userDetails.getUsername());

            // 4. Obtener el Usuario de tu base de datos para los detalles extra (id, roles)
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la DB después de autenticación exitosa."));

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // 5. Devolver la respuesta JWT
            return ResponseEntity.ok(new JwtResponse(jwt, usuario.getIdUsuario(), userDetails.getUsername(), roles.toArray(new String[0])));

        } catch (Exception e) {
            // Manejar errores de autenticación (ej. credenciales inválidas)
            return ResponseEntity.badRequest().body("Error de autenticación: " + e.getMessage());
        }
    }

    
}