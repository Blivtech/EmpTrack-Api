package com.emptrack.api.controller;


import com.emptrack.api.dto.WorkEntryRequest;
import com.emptrack.api.dto.WorkEntryResponse;
import com.emptrack.api.dto.WorkSummaryResponse;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.WorkEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
public class WorkEntryController {

    private final WorkEntryService workEntryService;

    // ✅ Add work entries
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(
        @RequestBody WorkEntryRequest req
    ) {
        workEntryService.addWorkEntries(req);
        return ResponseEntity.ok(ApiResponse.success("Work entry saved successfully","[]"));
    }

    // ✅ Get entries — by date or month
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkEntryResponse>>> getEntries(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam(required = false) String date,
        @RequestParam(required = false) String month
    ) {
        List<WorkEntryResponse> list;

        if (date != null && !date.isEmpty()) {
            list = workEntryService.getByDate(btCode, companyCode, date);
        } else if (month != null && !month.isEmpty()) {
            list = workEntryService.getByMonth(btCode, companyCode, month);
        } else {
            list = workEntryService.getByMonth(
                btCode, companyCode,
                java.time.LocalDate.now().toString().substring(0, 7)
            );
        }

        return ResponseEntity.ok(ApiResponse.success("",list));
    }

    // ✅ Monthly summary
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<WorkSummaryResponse>>> getSummary(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String month
    ) {
        List<WorkSummaryResponse> list =
            workEntryService.getMonthlySummary(btCode, companyCode, month);
        return ResponseEntity.ok(ApiResponse.success("",list));
    }

    // ✅ Delete entry
    @DeleteMapping("/{entryId}")
    public ResponseEntity<ApiResponse<String>> delete(
        @PathVariable String entryId
    ) {
        workEntryService.deleteEntry(entryId);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully","[]"));
    }
}