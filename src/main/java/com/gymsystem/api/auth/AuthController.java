package com.gymsystem.api.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymsystem.api.auth.dto.AuthResponse;
import com.gymsystem.api.auth.dto.LoginRequest;
import com.gymsystem.api.security.JwtUtil;
import com.gymsystem.api.user.User;
import com.gymsystem.api.user.dto.UpdatePasswordRequest;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5173"})
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User loggedInUser = authService.loginUser(request);

        // 1. Fabricamos el token criptográfico
        String token = jwtUtil.generateToken(loggedInUser.getEmail(), loggedInUser.getRoleId());

        // 2. Preparamos la respuesta segura sin contraseña, puro token
        AuthResponse response = new AuthResponse(token, 
            loggedInUser.getEmail(), 
            loggedInUser.getRoleId(), 
            loggedInUser.isNeedsPasswordChange()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        // Obtenemos el correo directamente del token verificado
        // El usuario no puede falsificar esto porque el token está firmado criptográficamente.
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        authService.updateFirstPassword(userEmail, request.getNewPassword());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contraseña actualizada exitosamente.");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}
