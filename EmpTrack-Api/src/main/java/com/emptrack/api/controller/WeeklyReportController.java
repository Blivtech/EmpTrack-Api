package com.emptrack.api.controller;

import com.emptrack.api.dto.*;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.WeeklyReportService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports/attendance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService service;

    // ✅ Weekly summary — all shifts
    @GetMapping("/weekly")

    public ResponseEntity<ApiResponse<WeeklyReportResponse>> getWeeklySummary(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String weekStart,
        @RequestParam String weekEnd
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("",
                service.getWeeklySummary(
                    btCode, companyCode, weekStart, weekEnd
                )
            )
        );
    }

    // ✅ Shift employee list — Present or Absent
    @GetMapping("/weekly/employees")
    public ResponseEntity<ApiResponse<List<WeeklyShiftEmployeeResponse>>> getShiftEmployees(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String weekStart,
        @RequestParam String weekEnd,
        @RequestParam String shiftCode,
        @RequestParam String type       // PRESENT or ABSENT
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("",
                service.getShiftEmployees(
                    btCode, companyCode,
                    weekStart, weekEnd,
                    shiftCode, type
                )
            )
        );
    }

    // ✅ Employee weekly detail — day by day
    @GetMapping("/weekly/employee-detail")
    public ResponseEntity<ApiResponse<EmployeeWeeklyDetailResponse>> getEmployeeDetail(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String weekStart,
        @RequestParam String weekEnd,
        @RequestParam String shiftCode,
        @RequestParam String empCode
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("",
                service.getEmployeeDetail(
                    btCode, companyCode,
                    weekStart, weekEnd,
                    shiftCode, empCode
                )
            )
        );
    }
}