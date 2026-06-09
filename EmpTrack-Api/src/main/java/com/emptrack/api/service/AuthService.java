package com.emptrack.api.service;

import com.emptrack.api.Utility.JwtUtil;
import com.emptrack.api.dto.LoginRequest;
import com.emptrack.api.dto.LoginResponse;
import com.emptrack.api.dto.RegisterRequest;
import com.emptrack.api.dto.RegisterResponse;
import com.emptrack.api.model.TblBtSequence;
import com.emptrack.api.model.TblUsers;
import com.emptrack.api.repository.BtSequenceRepository;
import com.emptrack.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BtSequenceRepository btSequenceRepository;
    private final JwtUtil jwtUtil;

    // Generate BT0001, BT0002...
    private String generateBtCode() {
        TblBtSequence seq = btSequenceRepository.save(new TblBtSequence());
        return String.format("BT%04d", seq.getId());
    }

    public RegisterResponse register(RegisterRequest request) {

        // Check duplicate phone
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }

        // Check duplicate email
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Map request to entity
        TblUsers user = new TblUsers();
        user.setDisplayName(request.getDisplayName());
        user.setUsername(request.getUsername());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setWhatsappNumber(request.getWhatsappNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(request.getUserType());
        user.setReferralId(request.getReferralId());
        user.setAddress(request.getAddress());
        user.setReportTo(request.getReportTo());
        user.setFcmToken(request.getFcmToken());
        user.setDeviceId(request.getDeviceId());
        user.setDeviceName(request.getDeviceName());
        user.setAppVersion(request.getAppVersion());
        user.setActiveStatus(1);
        user.setBtCode(generateBtCode());

        // Save
        TblUsers saved = userRepository.save(user);

        // Map to response DTO
        RegisterResponse response = new RegisterResponse();
        response.setId(saved.getId());
        response.setBtCode(saved.getBtCode());
        response.setUsername(saved.getUsername());
        response.setDisplayName(saved.getDisplayName());
        response.setPhoneNumber(saved.getPhoneNumber());
        response.setEmail(saved.getEmail());
        response.setUserType(saved.getUserType());
        response.setReportTo(saved.getReportTo());
        response.setActiveStatus(saved.getActiveStatus());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    public LoginResponse login(LoginRequest request) {

        // Find user by phone
        TblUsers user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check active status
        if (user.getActiveStatus() != 1) {
            throw new RuntimeException("Account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Update FCM token & device info on every login
        user.setFcmToken(request.getFcmToken());
        user.setDeviceId(request.getDeviceId());
        user.setDeviceName(request.getDeviceName());
        user.setAppVersion(request.getAppVersion());
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT
        String token = jwtUtil.generateToken(user.getId(), user.getPhoneNumber(), user.getUserType());

        return new LoginResponse(
                user.getId(),
                user.getBtCode(),
                user.getDisplayName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getUserType(),
                token
        );
    }
}