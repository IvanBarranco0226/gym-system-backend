package com.gymsystem.api.user.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UpdateEmployeeRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Integer roleId;
    private String phone;
    private String nss;
    private BigDecimal salary;
    private String shift;
}
