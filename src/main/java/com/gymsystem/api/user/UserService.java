package com.gymsystem.api.user;

import com.gymsystem.api.auth.dto.LoginRequest;
import com.gymsystem.api.user.dto.RegisterUserRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(RegisterUserRequest request){
        // Regla de negocio 1: Verificar que el email no este en uso
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("El correo ya está registrado en el sistema.");
        }

        // Creamos la entidad real que irá a la base de datos
        User newUser = new User();
        newUser.setEmail(request.getEmail());

        // Regla de negocio 2: Encriptación obligatoria
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        newUser.setRoleId(request.getRoleId());
        
        return userRepository.save(newUser);
    }

    public User loginUser(LoginRequest request){
        // 1. Buscamos al usuario. Si no existe, lanzamos error genérico por seguridad (no dar pistas)
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // 2. Comparamos la contraseña en texto plano con el hash de la BD
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new RuntimeException("Credenciales inválidas");
        }

        // Si pasa, devolvemos el usuario (EL controlador se encargará de ocultar el hash)
        return user;
    }
}
