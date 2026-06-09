package com.emptrack.api.controller;

import com.emptrack.api.dto.CompanyRequest;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> saveCompany(@RequestBody CompanyRequest request) {
        ApiResponse<?> response = companyService.saveCompany(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateCompany(
            @PathVariable String id,
            @RequestBody CompanyRequest request) {
        ApiResponse<?> response = companyService.updateCompany(id, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteCompany(@PathVariable Long id) {
        ApiResponse<?> response = companyService.deleteCompany(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCompanies(@RequestParam String btCode) {
        ApiResponse<?> response = companyService.getAllCompanies(btCode);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}