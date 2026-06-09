package com.emptrack.api.controller;

import com.emptrack.api.model.TblDepartment;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> saveDepartment(@RequestBody TblDepartment request) {
        ApiResponse<?> response = departmentService.saveDepartment(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}