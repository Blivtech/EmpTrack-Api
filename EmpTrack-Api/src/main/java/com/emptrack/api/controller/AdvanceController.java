package com.emptrack.api.controller;

import com.emptrack.api.dto.AdvanceRequest;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.AdvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/advance")
public class AdvanceController {

    @Autowired
    private AdvanceService advanceService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> saveAdvances(@RequestBody AdvanceRequest request) {
        ApiResponse<?> response = advanceService.saveAdvances(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateAdvance(
            @PathVariable Long id,
            @RequestBody AdvanceRequest.AdvanceItem request) {
        ApiResponse<?> response = advanceService.updateAdvance(id, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteAdvance(@PathVariable Long id) {
        ApiResponse<?> response = advanceService.deleteAdvance(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/employee/{empCode}")
    public ResponseEntity<ApiResponse<?>> getByEmployee(@PathVariable String empCode) {
        ApiResponse<?> response = advanceService.getByEmployee(empCode);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/company")
    public ResponseEntity<ApiResponse<?>> getByCompany(
            @RequestParam String companyCode,
            @RequestParam String btCode) {
        ApiResponse<?> response = advanceService.getByCompany(companyCode, btCode);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}