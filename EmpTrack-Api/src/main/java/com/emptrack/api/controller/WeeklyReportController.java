package com.emptrack.api.controller;

import com.emptrack.api.service.WeeklyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/attendance/weekly")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService service;

    // ✅ Weekly summary — all shifts
    @GetMapping
    public ResponseEntity<?> getWeeklyReport(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String weekStart,
        @RequestParam String weekEnd
    ) {
        return ResponseEntity.ok(
            service.getWeeklyReport(
                btCode, companyCode,
                weekStart, weekEnd
            )
        );
    }

    // ✅ Shift employee list
    @GetMapping("/employees")
    public ResponseEntity<?> getShiftEmployees(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String weekStart,
        @RequestParam String weekEnd,
        @RequestParam String shiftCode,
        @RequestParam String type       // PRESENT / ABSENT / HOLIDAY_WO
    ) {
        return ResponseEntity.ok(
            service.getShiftEmployees(
                btCode, companyCode,
                weekStart, weekEnd,
                shiftCode, type
            )
        );
    }

    // ✅ Employee weekly detail
    @GetMapping("/employee-detail")
    public ResponseEntity<?> getEmployeeWeeklyDetail(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String weekStart,
        @RequestParam String weekEnd,
        @RequestParam String shiftCode,
        @RequestParam String empCode
    ) {
        return ResponseEntity.ok(
            service.getEmployeeWeeklyDetail(
                btCode, companyCode,
                weekStart, weekEnd,
                shiftCode, empCode
            )
        );
    }

    // ✅ Weekly overall — all employees all shifts
    @GetMapping("/overall")
    public ResponseEntity<?> getWeeklyOverallReport(
            @RequestParam String btCode,
            @RequestParam String companyCode,
            @RequestParam String weekStart,
            @RequestParam String weekEnd
    ) {
        return ResponseEntity.ok(
                service.getWeeklyOverallReport(
                        btCode, companyCode, weekStart, weekEnd
                )
        );
    }

    // ✅ Weekly shift wise — employees for specific shift
    @GetMapping("/shift")
    public ResponseEntity<?> getWeeklyShiftReport(
            @RequestParam String btCode,
            @RequestParam String companyCode,
            @RequestParam String weekStart,
            @RequestParam String weekEnd,
            @RequestParam String shiftCode
    ) {
        return ResponseEntity.ok(
                service.getWeeklyShiftReport(
                        btCode, companyCode,
                        weekStart, weekEnd, shiftCode
                )
        );
    }

}