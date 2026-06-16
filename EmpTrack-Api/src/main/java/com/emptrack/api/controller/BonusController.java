package com.emptrack.api.controller;


import com.emptrack.api.dto.BonusRequest;
import com.emptrack.api.dto.BonusResponse;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.BonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bonus")
@RequiredArgsConstructor
public class BonusController {

    private final BonusService bonusService;

    // ✅ Add bonus
    // ✅ Add bonus
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(
            @RequestBody BonusRequest req
    ) {
        bonusService.addBonus(req);
        return ResponseEntity.ok(ApiResponse.success("Bonus saved successfully","[]"));
    }
//
//    // ✅ Get all — optional month filter
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<BonusResponse>>> getAll(
//        @RequestParam String btCode,
//        @RequestParam String companyCode,
//        @RequestParam(required = false) String month
//    ) {
//        List<BonusResponse> list = (month != null && !month.isEmpty())
//            ? bonusService.getByMonth(btCode, companyCode, month)
//            : bonusService.getAll(btCode, companyCode);
//        return ResponseEntity.ok(ApiResponse.success(list));
//    }
//
//    // ✅ Mark paid
//    @PutMapping("/paid/{bonusId}")
//    public ResponseEntity<ApiResponse<BonusResponse>> markPaid(
//        @PathVariable String bonusId
//    ) {
//        BonusResponse response = bonusService.markPaid(bonusId);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    // ✅ Delete
//    @DeleteMapping("/{bonusId}")
//    public ResponseEntity<ApiResponse<String>> delete(
//        @PathVariable String bonusId
//    ) {
//        bonusService.delete(bonusId);
//        return ResponseEntity.ok(ApiResponse.success("Deleted successfully"));
//    }
}