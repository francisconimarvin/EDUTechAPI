package com.example.EdutechAPI.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor 
@AllArgsConstructor 
public class JwtResponse {
    private String token;
    private String type = "Bearer"; // Tipo de token, por convención "Bearer"
    private Long id;
    private String email;
    private String[] roles; // Array de roles del usuario

    // Constructor para un login exitoso
    public JwtResponse(String accessToken, Long id, String email, String[] roles) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.roles = roles;
    }
}