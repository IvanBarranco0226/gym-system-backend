package com.gymsystem.api.config;

import com.gymsystem.api.user.User;
import com.gymsystem.api.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner{
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificamos si el admin maestro ya existe para no duplicarlo
        String adminEmail = "admin@gym.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()){
            User admin = new User();
            admin.setEmail(adminEmail);
            // NUnca gyardamos texto plano, usamos nuestro Bcrypt
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            // 1 = ID de rol 'admin' que metimos en sql
            admin.setRoleId(1);
            userRepository.save(admin);
            System.out.println("Admin maestro creado exitosamente");
        }
    }
}
