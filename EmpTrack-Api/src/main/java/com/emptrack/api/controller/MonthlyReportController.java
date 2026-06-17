package com.emptrack.api.controller;

import com.emptrack.api.service.MonthlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/attendance/monthly")
@RequiredArgsConstructor
public class MonthlyReportController {

    private final MonthlyReportService service;

    // ✅ Overall monthly report
    @GetMapping
    public ResponseEntity<?> getMonthlyReport(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String month          // "2026-06"
    ) {
        return ResponseEntity.ok(
            service.getMonthlyReport(btCode, companyCode, month)
        );
    }

    // ✅ Shift wise monthly report
    @GetMapping("/shift")
    public ResponseEntity<?> getMonthlyShiftReport(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String month,
        @RequestParam String shiftCode
    ) {
        return ResponseEntity.ok(
            service.getMonthlyShiftReport(
                btCode, companyCode, month, shiftCode
            )
        );
    }

    // ✅ Employee detail
    @GetMapping("/employee-detail")
    public ResponseEntity<?> getMonthlyEmployeeDetail(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String month,
        @RequestParam String shiftCode,
        @RequestParam String empCode
    ) {
        return ResponseEntity.ok(
            service.getMonthlyEmployeeDetail(
                btCode, companyCode, month, shiftCode, empCode
            )
        );
    }
}