package com.gymsystem.api.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gymsystem.api.exception.AuthException;
import com.gymsystem.api.auth.dto.LoginRequest;
import com.gymsystem.api.user.User;
import com.gymsystem.api.user.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User loginUser(LoginRequest request) {
        // 1. Buscamos al usuario. Si no existe, lanzamos error generico por seguridad
        User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthException("Credenciales inválidas"));
        
        // 2. Comparamos la contraseña en texto plano con el hash de la BD
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Credenciales inválidas");
        }

        // 3. Devolvemos el usuario, se oculta el hash desde el controlador
        return user;
    }

    @Transactional
    public void updateFirstPassword(String email, String newPassword) {
        // 1. Buscamos al usuario basado en el correo que extrajimos del token
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Encriptamos la nueva contraseña
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        // 3. Apagamos la bandera: ya no le pediremos cambiarla en el futuro
        user.setNeedsPasswordChange(false);

        // 4. Guardamos
        userRepository.save(user);
    }
    
}
