package com.emptrack.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegisterResponse {
    private Long id;
    private String btCode;
    private String username;
    private String displayName;
    private String phoneNumber;
    private String email;
    private Integer userType;
    private Integer reportTo;
    private Integer activeStatus;
    private LocalDateTime createdAt;
}