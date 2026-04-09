package com.gymsystem.api.client;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "client_profiles")
@Data
public class ClientProfile {
    // En nuestra BD, el user_id es la llave primaria y foránea al mismo tiempo
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName; 

    @Column(name = "numeric_code", unique = true, length = 50)
    private String numericCode;

    @Column(name = "is_active")
    private Boolean isActive; 
}
