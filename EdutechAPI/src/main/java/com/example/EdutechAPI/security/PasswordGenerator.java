package com.example.EdutechAPI.security; 

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Password123"; // Cambio, en lugar de hash_passwordNUMBER usaremos esto.
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Contraseña plana: " + rawPassword);
        System.out.println("Contraseña BCrypt encriptada: " + encodedPassword);
    }
}