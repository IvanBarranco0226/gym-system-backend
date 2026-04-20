package com.gymsystem.api.user;

import com.gymsystem.api.ApiApplication;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.gymsystem.api.auth.dto.AuthResponse;
import com.gymsystem.api.auth.dto.LoginRequest;
import com.gymsystem.api.security.JwtUtil;
import com.gymsystem.api.user.dto.EmployeeResponse;
import com.gymsystem.api.user.dto.RegisterEmployeeRequest;
import com.gymsystem.api.user.dto.RegisterUserRequest;
import com.gymsystem.api.user.dto.UpdateEmployeeRequest;
import com.gymsystem.api.user.dto.UpdatePasswordRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;





@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5173"})
public class UserController {

    private final ApiApplication apiApplication;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    UserController(ApiApplication apiApplication) {
        this.apiApplication = apiApplication;
    }

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
                                    loggedInUser.getRoleId(),
                                    loggedInUser.isNeedsPasswordChange()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<String> getDashboardStatus() {
        // Si la petición llega hasta aquí, significa que el JWT es válido
        return ResponseEntity.ok("{\"clientesActivos\": 150, \"instructores\": 5}");
    }

    // 1. Seguridad DevSecOps: SOlo el admin maestro puede tocar esta ruta
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/employee/register")
    public ResponseEntity<?> registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request){
        // 2. COdigo limpio no hay try catch si el correo existe,
        // el UserService lanzará el error y el globalExceptionHandler lo atrapará
        userService.registerEmployee(request);
        
        //Creamos un mapa que Spring boot convertirá a JSON magicamente
        Map<String, String> response = new HashMap<>();
        response.put("message", "Empleado registrado exitosamente.");
        response.put("status", "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/employee/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable UUID id) {
        userService.deleteEmployee(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Empleado eliminado exitosamente.");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/employee/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest request) {
        userService.updateEmployee(id, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Empleado actualizado exitosamente.");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        // Obtenemos el correo directamente del token verificado
        // El usuario no puede falsificar esto porque el token está firmado criptográficamente.apiApplication
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        userService.updateFirstPassword(userEmail, request.getNewPassword());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contraseña actualizada exitosamente.");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
    
    
} 
