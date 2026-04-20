package com.gymsystem.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String name, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ivanbarranco0226@gmail.com"); // El correo que autorizaste en Brevo
        message.setTo(to);
        message.setSubject("Bienvenido al Equipo - Tus credenciales de acceso");
        message.setText("Hola " + name + ", \n\n" + 
                "Tu cuenta ha sido creada exitosamente. \n" + 
                "Usuario: " + to + "\n" +
                "Contraseña temporal: " + password + "\n\n" + 
                "Por seguridad, el sistema te pedirá cambiarla al iniciar sesión.\n\n" +
                "Atentamente,\n" +
                "Tu Gym"
        );
        mailSender.send(message);
    }
}
