package com.emptrack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String btCode;
    private String displayName;
    private String phoneNumber;
    private String email;
    private Integer userType;
    private String token;       // JWT token
}