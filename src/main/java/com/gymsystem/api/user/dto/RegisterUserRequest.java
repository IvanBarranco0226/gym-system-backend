package com.gymsystem.api.user.dto;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String email;
    private String password;
    private Integer roleId;
}
