package com.emptrack.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String whatsappNumber;

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private Integer userType; // 1=Contractor, 2=Manager, 3=Admin

    private String referralId;
    private String address;
    private Integer reportTo;
    private String fcmToken;
    private String deviceId;
    private String deviceName;
    private String appVersion;
}