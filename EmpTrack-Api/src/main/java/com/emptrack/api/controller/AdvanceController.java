package com.emptrack.api.controller;


import com.emptrack.api.dto.AdvanceRequest;
import com.emptrack.api.dto.AdvanceResponse;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.AdvanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/advance")
@RequiredArgsConstructor
public class AdvanceController {

    private final AdvanceService advanceService;

// ✅ Add advance
@PostMapping("/add")
public ResponseEntity<ApiResponse<String>> add(
    @RequestBody AdvanceRequest req
) {
    advanceService.addAdvance(req);
    return ResponseEntity.ok(ApiResponse.success("Advance saved successfully","[]"));
}

//    // ✅ Get all — optional month filter
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<AdvanceResponse>>> getAll(
//        @RequestParam String btCode,
//        @RequestParam String companyCode,
//        @RequestParam(required = false) String month
//    ) {
//        List<AdvanceResponse> list = (month != null && !month.isEmpty())
//            ? advanceService.getByMonth(btCode, companyCode, month)
//            : advanceService.getAll(btCode, companyCode);
//        return ResponseEntity.ok(ApiResponse.success(list));
//    }
//
//    // ✅ Mark recovered
//    @PutMapping("/recover/{advanceId}")
//    public ResponseEntity<ApiResponse<AdvanceResponse>> markRecovered(
//        @PathVariable String advanceId
//    ) {
//        AdvanceResponse response = advanceService.markRecovered(advanceId);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    // ✅ Delete
//    @DeleteMapping("/{advanceId}")
//    public ResponseEntity<ApiResponse<String>> delete(
//        @PathVariable String advanceId
//    ) {
//        advanceService.delete(advanceId);
//        return ResponseEntity.ok(ApiResponse.success("Deleted successfully"));
//    }
}