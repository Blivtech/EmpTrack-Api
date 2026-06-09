package com.emptrack.api.controller;

import com.emptrack.api.dto.AttendanceRequest;
import com.emptrack.api.dto.*;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;

    // ✅ Mark attendance — POST
    @PostMapping("/mark")
    public ResponseEntity<ApiResponse<AttendanceResponse>> mark(
        @RequestBody AttendanceRequest request
    ) {
        try {
            AttendanceResponse response =
                attendanceService.markAttendance(request);
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Attendance marked successfully", response)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(400, e.getMessage(), null)
            );
        }
    }

    // ✅ Update attendance — PUT
    @PutMapping("/update/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> update(
        @PathVariable String attendanceId,
        @RequestBody AttendanceRequest request
    ) {
        try {
            AttendanceResponse response =
                attendanceService.updateAttendance(attendanceId, request);
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Attendance updated successfully", response)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(400, e.getMessage(), null)
            );
        }
    }

    // ✅ Get attendance by attendanceId — GET
    @GetMapping("/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getById(
        @PathVariable String attendanceId
    ) {
        try {
            AttendanceResponse response =
                attendanceService.getByAttendanceId(attendanceId);
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Success", response)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(400, e.getMessage(), null)
            );
        }
    }

    // ✅ Check attendance — GET
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> check(
        @RequestParam String btCode,
        @RequestParam String companyId,
        @RequestParam String shiftId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Map<String, Object> result =
            attendanceService.checkAttendance(btCode, companyId, shiftId, date);
        return ResponseEntity.ok(
            new ApiResponse<>(200, "Success", result)
        );
    }

    // ✅ Get today's all shifts status — GET
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<ShiftStatusResponse>>> today(
        @RequestParam String btCode,
        @RequestParam String companyId,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<ShiftStatusResponse> response =
            attendanceService.getTodayStatus(btCode, companyId, targetDate);
        return ResponseEntity.ok(
            new ApiResponse<>(200, "Success", response)
        );
    }
}