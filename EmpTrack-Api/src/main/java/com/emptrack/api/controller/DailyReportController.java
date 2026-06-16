package com.emptrack.api.controller;


import com.emptrack.api.dto.AttendanceEmployeeResponse;
import com.emptrack.api.dto.DailyReportSummaryResponse;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports/attendance")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService service;

    // ✅ Daily summary — all shifts
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyReportSummaryResponse>> getDailySummary(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam(required = false) String date
    ) {
        // ✅ Default to today if no date
        String reportDate = (date != null && !date.isEmpty())
            ? date
            : LocalDate.now().toString();

        return ResponseEntity.ok(
            ApiResponse.success("",
                service.getDailySummary(btCode, companyCode, reportDate)
            )
        );
    }

    // ✅ Employee list — by shift + type
    @GetMapping("/daily/employees")
    public ResponseEntity<ApiResponse<List<AttendanceEmployeeResponse>>> getEmployees(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String date,
        @RequestParam String shiftCode,
        @RequestParam String type       // PRESENT or LEAVE
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("",
                service.getEmployeesByShiftAndType(
                    btCode, companyCode, date, shiftCode, type
                )
            )
        );
    }
}