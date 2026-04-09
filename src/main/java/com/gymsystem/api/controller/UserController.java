package com.gymsystem.api.controller;

import com.gymsystem.api.dto.AuthResponse;
import com.gymsystem.api.dto.LoginRequest;
import com.gymsystem.api.dto.RegisterUserRequest;
import com.gymsystem.api.model.User;
import com.gymsystem.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gymsystem.api.security.JwtUtil;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5173"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // El endpoint para dar de alta. Más adelante le pondremos:
    // @PreAUtorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest request){
        try {
            User registeredUser = userService.registerNewUser(request);
            return ResponseEntity.ok("Usuario registrado exitosamente con ID: " + registeredUser.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        try {
            User loggedInUser = userService.loginUser(request);

            // 1. Fabricamos el token Criptográfico
            String token = jwtUtil.generateToken(loggedInUser.getEmail(), loggedInUser.getRoleId());

            // 2. Preparamos la respuesta segura sin contraseña, puro token
            AuthResponse response = new AuthResponse(token,
                                    loggedInUser.getEmail(),
                                    loggedInUser.getRoleId()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
} 
