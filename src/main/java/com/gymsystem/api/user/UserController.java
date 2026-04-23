package com.gymsystem.api.user;

import com.gymsystem.api.Employee.dto.EmployeeResponse;
import com.gymsystem.api.Employee.dto.RegisterEmployeeRequest;
import com.gymsystem.api.Employee.dto.UpdateEmployeeRequest;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;





@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5173"})
public class UserController {

    @Autowired
    private UserService userService;

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
    
} 
