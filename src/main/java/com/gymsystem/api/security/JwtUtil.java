package com.gymsystem.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // Importamos los valores de tu application.yml
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Preparamos la llave criptográfica
    private Key getSingingKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // El método que fabrica el token
    public String generateToken(String email, Integer roleId){
        return Jwts.builder()
        .setSubject(email)
        .claim("role", roleId) // Guardamos el rol dentro del token
        .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
        .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Fecha de expiración
        .signWith(getSingingKey(), SignatureAlgorithm.HS256) // Firma digital
        .compact(); // Lo compactamos para obtener el String final
    }

    // 1. Extraer el correo (subject) del token
    public String extractUsername(String token){
        return Jwts.parserBuilder()
        .setSigningKey(getSingingKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
    }

    // 2. Extraer el ID del rol que guardamos dentro
    public Integer extractRole(String token){
        return Jwts.parserBuilder()
        .setSigningKey(getSingingKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("role", Integer.class);
    }

    // 3. Validar que el token sea auténtico y no haya expirado
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
            .setSigningKey(getSingingKey())
            .build()
            .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // SI la firma es falsa, está expirado o malformado, cae aquí
            return false;
        }
    }
    
}
