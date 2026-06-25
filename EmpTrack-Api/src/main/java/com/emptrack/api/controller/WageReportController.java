package com.emptrack.api.controller;

import com.emptrack.api.dto.AdvanceEntryRequest;
import com.emptrack.api.dto.BonusEntryRequest;
import com.emptrack.api.dto.OvertimeEntryRequest;
import com.emptrack.api.service.WageReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class WageReportController {

    private final WageReportService reportService;

    // ✅ Advance — monthly report
    @GetMapping("/advance")
    public ResponseEntity<?> getAdvanceReport(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String month
    ) {
        return ResponseEntity.ok(
            reportService.getAdvanceList(btCode, companyCode, month)
        );
    }


    @PutMapping("/advance/{advanceId}")
    public ResponseEntity<?> updateAdvance(
            @PathVariable String advanceId,
            @RequestBody AdvanceEntryRequest request
    ) {
        return ResponseEntity.ok(reportService.updateAdvance(advanceId, request));
    }

    @DeleteMapping("/advance/{advanceId}")
    public ResponseEntity<?> deleteAdvance(
            @PathVariable String advanceId,
            @RequestParam String btCode,
            @RequestParam String companyCode
    ) {
        return ResponseEntity.ok(reportService.deleteAdvance(advanceId, btCode, companyCode));
    }

    // ✅ Bonus — monthly report
    @GetMapping("/bonus")
    public ResponseEntity<?> getBonusReport(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String month
    ) {
        return ResponseEntity.ok(
            reportService.getBonusList(btCode, companyCode, month)
        );
    }

    @PutMapping("/bonus/{bonusId}")
    public ResponseEntity<?> updateBonus(
            @PathVariable String bonusId,
            @RequestBody BonusEntryRequest request
    ) {
        return ResponseEntity.ok(reportService.updateBonus(bonusId, request));
    }

    @DeleteMapping("/bonus/{bonusId}")
    public ResponseEntity<?> deleteBonus(
            @PathVariable String bonusId,
            @RequestParam String btCode,
            @RequestParam String companyCode
    ) {
        return ResponseEntity.ok(reportService.deleteBonus(bonusId, btCode, companyCode));
    }

    // ✅ Overtime — monthly report
    @GetMapping("/overtime")
    public ResponseEntity<?> getOvertimeReport(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String month
    ) {
        return ResponseEntity.ok(
            reportService.getOvertimeList(btCode, companyCode, month)
        );
    }
    @PutMapping("/overtime/{otId}")
    public ResponseEntity<?> updateOvertime(
            @PathVariable String otId,
            @RequestBody OvertimeEntryRequest request
    ) {
        return ResponseEntity.ok(reportService.updateOvertime(otId, request));
    }

    @DeleteMapping("/overtime/{otId}")
    public ResponseEntity<?> deleteOvertime(
            @PathVariable String otId,
            @RequestParam String btCode,
            @RequestParam String companyCode
    ) {
        return ResponseEntity.ok(reportService.deleteOvertime(otId, btCode, companyCode));
    }
}