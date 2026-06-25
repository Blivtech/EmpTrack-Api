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

}