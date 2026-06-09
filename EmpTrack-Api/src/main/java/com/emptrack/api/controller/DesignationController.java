package com.emptrack.api.controller;

import com.emptrack.api.model.TblDesignation;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/designations")
public class DesignationController {

    @Autowired
    private DesignationService designationService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> saveDesignation(@RequestBody TblDesignation request) {
        ApiResponse<?> response = designationService.saveDesignation(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}