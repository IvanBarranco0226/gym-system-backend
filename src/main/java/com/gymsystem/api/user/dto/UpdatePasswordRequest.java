package com.gymsystem.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "Debe tener al menos 8 caracteres")
    @Pattern(regexp = ".*[A-Z].*", message = "Debe contener al menos una mayúscula")
    @Pattern(regexp = ".*[a-z].*", message = "Debe contener al menos una minúscula")
    @Pattern(regexp = ".*\\d.*", message = "Debe contener al menos un número")
    @Pattern(regexp = ".*[@$!%*?&].*", message = "Debe contener al menos un carácter especial")
    private String newPassword;
}
