package com.emptrack.api.controller;

import com.emptrack.api.dto.*;
import com.emptrack.api.model.TblUsers;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // ✅ Login
    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(
            @RequestBody AdminLoginRequest req
    ) {
        return ResponseEntity.ok(
                authService.loginAdmin(req)
        );
    }
}