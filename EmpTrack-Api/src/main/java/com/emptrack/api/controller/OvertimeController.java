package com.emptrack.api.controller;


import com.emptrack.api.dto.OvertimeRequest;
import com.emptrack.api.dto.OvertimeResponse;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.OvertimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    // ✅ Add overtime
// ✅ Add overtime
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(
            @RequestBody OvertimeRequest req
    ) {
        overtimeService.addOvertime(req);
        return ResponseEntity.ok(ApiResponse.success("Overtime saved successfully","[]"));
    }

//    // ✅ Get all — optional month filter
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<OvertimeResponse>>> getAll(
//        @RequestParam String btCode,
//        @RequestParam String companyCode,
//        @RequestParam(required = false) String month
//    ) {
//        List<OvertimeResponse> list = (month != null && !month.isEmpty())
//            ? overtimeService.getByMonth(btCode, companyCode, month)
//            : overtimeService.getAll(btCode, companyCode);
//        return ResponseEntity.ok(ApiResponse.success(list));
//    }

//    // ✅ Mark paid
//    @PutMapping("/paid/{otId}")
//    public ResponseEntity<ApiResponse<OvertimeResponse>> markPaid(
//        @PathVariable String otId
//    ) {
//        OvertimeResponse response = overtimeService.markPaid(otId);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    // ✅ Delete
//    @DeleteMapping("/{otId}")
//    public ResponseEntity<ApiResponse<String>> delete(
//        @PathVariable String otId
//    ) {
//        overtimeService.delete(otId);
//        return ResponseEntity.ok(ApiResponse.success("Deleted successfully"));
//    }
}