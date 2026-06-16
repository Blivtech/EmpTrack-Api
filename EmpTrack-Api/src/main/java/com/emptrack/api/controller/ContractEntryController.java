package com.emptrack.api.controller;


import com.emptrack.api.dto.ContractEntryRequest;
import com.emptrack.api.dto.ContractEntryResponse;
import com.emptrack.api.dto.ContractSummaryResponse;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.ContractEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contract-entries")
@RequiredArgsConstructor
public class ContractEntryController {

    private final ContractEntryService service;

    // ✅ Add entries
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(
        @RequestBody ContractEntryRequest req
    ) {
        service.addEntries(req);
        return ResponseEntity.ok(
            ApiResponse.success("Entries saved successfully","")
        );
    }

    // ✅ Get entries — by month or date+shift
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractEntryResponse>>> get(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam(required = false) String month,
        @RequestParam(required = false) String date,
        @RequestParam(required = false) String shiftCode
    ) {
        List<ContractEntryResponse> list;

        if (date != null && shiftCode != null) {
            list = service.getByDateShift(btCode, companyCode, date, shiftCode);
        } else {
            String m = month != null ? month :
                LocalDate.now().toString().substring(0, 7);
            list = service.getByMonth(btCode, companyCode, m);
        }

        return ResponseEntity.ok(ApiResponse.success("",list));
    }

    // ✅ Monthly summary
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ContractSummaryResponse>> summary(
        @RequestParam String btCode,
        @RequestParam String companyCode,
        @RequestParam String month
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("",
                service.getMonthlySummary(btCode, companyCode, month)
            )
        );
    }

    // ✅ Delete entry
    @DeleteMapping("/{entryId}")
    public ResponseEntity<ApiResponse<String>> delete(
        @PathVariable String entryId
    ) {
        service.deleteEntry(entryId);
        return ResponseEntity.ok(
            ApiResponse.success("Entry deleted successfully","")
        );
    }
}