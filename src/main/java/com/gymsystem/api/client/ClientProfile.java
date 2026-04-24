package com.gymsystem.api.client;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

import com.gymsystem.api.user.User;

@Entity
@Table(name = "client_profiles")
@Data
public class ClientProfile {
    // En nuestra BD, el user_id es la llave primaria y foránea al mismo tiempo
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "pin", length = 10)
    private String pin;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "emergency_contact", length = 15)
    private String emergencyContact;

    @Column(name = "is_active")
    private Boolean isActive; 
}
