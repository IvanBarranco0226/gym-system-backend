package com.gymsystem.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_logs")
@Data
public class AttendanceLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "entry_time", insertable = false, updatable = false)
    private LocalDateTime entryTime;
    
    @Column(name = "exit_time")
    private LocalDateTime exitTime;
}
