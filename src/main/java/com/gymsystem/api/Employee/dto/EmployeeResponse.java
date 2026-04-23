package com.gymsystem.api.Employee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class EmployeeResponse {
    private UUID id;
    private String email;
    private Integer roleId;
    private String nss;
    private BigDecimal salary;
    private String shift;
    private LocalDate hireDate;
    private String firstName;
    private String lastName;
    private String phone;
}
