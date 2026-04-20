package com.gymsystem.api.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RegisterEmployeeRequest {
    // Datos del usuario (Autenticación)
    @Email(message = "Formato de correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Pattern(regexp = ".*[A-Z].*", message = "La contraseña debe contener al menos una mayúscula")
    @Pattern(regexp = ".*[a-z].*", message = "La contraseña debe contener al menos una minúscula")
    @Pattern(regexp = ".*[0-9].*", message = "La contraseña debe contener al menos un número")
    private String password;
    @NotNull(message = "El rol es obligatorio")
    private Integer roleId;
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName; 
    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;

    // Datos del perfil laborales
    @NotBlank(message = "El NSS es obligatorio")
    @Size(min = 11, max = 11, message = "El NSS debe tener 11 dígitos")
    private String nss;
    @NotNull(message = "El salario es obligatorio")
    @Positive(message = "EL salario no puede ser negativo")
    private BigDecimal salary;
    @NotBlank(message = "El turno es obligatorio")
    private String shift;
    @NotNull(message = "La fecha de contratación es obligatoria")
    private LocalDate hireDate;
}
