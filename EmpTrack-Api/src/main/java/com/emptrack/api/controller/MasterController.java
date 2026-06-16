package com.emptrack.api.controller;

import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master")
public class MasterController {

    @Autowired
    private MasterService masterService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getMasterData(@RequestParam String btCode) {
        ApiResponse<?> response = masterService.getMasterData(btCode);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}