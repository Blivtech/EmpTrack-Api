package com.emptrack.api.controller;

import com.emptrack.api.model.TblEmployee;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> saveEmployee(@RequestBody TblEmployee request) {
        ApiResponse<?> response = employeeService.saveEmployee(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    // ✅ Update employee
    @PutMapping("/{empCode}/{companyCode}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable String empCode,
            @PathVariable String companyCode,
            @RequestBody  TblEmployee request
    ) {
        return ResponseEntity.ok(
                employeeService.updateEmployee(empCode, companyCode, request)
        );
    }

    @DeleteMapping("/{empCode}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable  String empCode,
            @RequestParam  String companyCode
    ) {
        return ResponseEntity.ok(
                employeeService.deleteEmployee(empCode, companyCode)
        );
    }

}