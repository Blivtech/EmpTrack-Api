package com.emptrack.api.dto;

import lombok.Data;


@Data
public class AdminLoginRequest {
    private String name;       // username or phone number
    private String password;
}