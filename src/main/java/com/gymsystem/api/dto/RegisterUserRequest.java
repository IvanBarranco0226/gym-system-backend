package com.gymsystem.api.dto;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String email;
    private String password;
    private Integer roleId;
}
