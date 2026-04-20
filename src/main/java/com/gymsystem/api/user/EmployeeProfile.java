package com.gymsystem.api.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "employee_profiles")
@Data
public class EmployeeProfile {

    @Id
    @Column(name = "user_id")
    private UUID id;

    // Este es el corazón de la relación
    @OneToOne
    @MapsId // Le dice a Spring "MI id es el mismo id de mi usuario padre"
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "nss", length = 20)
    private String nss;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

    @Column(name = "shift", length = 50)
    private String shift;

    @Column(name = "hire_date")
    private LocalDate hireDate;
}
