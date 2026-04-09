package com.gymsystem.api.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

        // 1. Buscamos el token en la cabecera
        String authHeader = request.getHeader("Authorization");

        // 2. Verificamos si existe y si tiene el formato correcto "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);

            // 3. Si el token es válido criptográficamente
            if (jwtUtil.validateToken(token)){
                String email = jwtUtil.extractUsername(token);
                Integer roleId = jwtUtil.extractRole(token);

                // 4. Traducimos tu ID numérico al formato que entiende Spring Security
                String roleName = roleId == 1 ? "ROLE_ADMIN": (roleId == 2 ? "ROLE_INSTRUCTOR": "ROLE_CLIENT");

                // 5. Creamos la credencial de acceso oficial
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email, null, List.of(new SimpleGrantedAuthority(roleName))
                );

                // 6. Le decimos a Spring Security: "Dejalo pasar, yo respondo por él"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Continúa con la cadena de peticiones (ya sea autorizado o bloqueado)
        filterChain.doFilter(request, response);
    }
    
}
