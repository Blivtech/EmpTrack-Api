package com.emptrack.api.controller;

import com.emptrack.api.model.TblEmployee;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> saveEmployee(@RequestBody TblEmployee request) {
        ApiResponse<?> response = employeeService.saveEmployee(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}