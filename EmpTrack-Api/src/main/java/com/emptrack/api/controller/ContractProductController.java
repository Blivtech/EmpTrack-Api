package com.emptrack.api.controller;


import com.emptrack.api.dto.ContractProductRequest;
import com.emptrack.api.dto.ContractProductResponse;
import com.emptrack.api.dto.ContractRateHistoryResponse;
import com.emptrack.api.dto.UpdateContractRateRequest;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.ContractProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contract-products")
@RequiredArgsConstructor
public class ContractProductController {

    private final ContractProductService service;

    // ✅ Add product
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(
        @RequestBody ContractProductRequest req
    ) {
        service.addProduct(req);
        return ResponseEntity.ok(
            ApiResponse.success("Product saved successfully","[]")
        );
    }

    // ✅ Get products
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractProductResponse>>> get(
        @RequestParam String btCode,
        @RequestParam String companyCode
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("",service.getProducts(btCode, companyCode))
        );
    }

    // ✅ Update product
    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<String>> update(
        @PathVariable String productId,
        @RequestBody ContractProductRequest req
    ) {
        service.updateProduct(productId, req);
        return ResponseEntity.ok(
            ApiResponse.success("Product updated successfully","")
        );
    }

    // ✅ Update rate
    @PutMapping("/rate/{productId}")
    public ResponseEntity<ApiResponse<String>> updateRate(
        @PathVariable String productId,
        @RequestBody UpdateContractRateRequest req
    ) {
        service.updateRate(productId, req);
        return ResponseEntity.ok(
            ApiResponse.success("Rate updated successfully","")
        );
    }

    // ✅ Delete product
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> delete(
        @PathVariable String productId
    ) {
        service.deleteProduct(productId);
        return ResponseEntity.ok(
            ApiResponse.success("Product deleted successfully","")
        );
    }

    // ✅ Rate history
    @GetMapping("/history/{productId}")
    public ResponseEntity<ApiResponse<List<ContractRateHistoryResponse>>> history(
        @PathVariable String productId
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("",service.getRateHistory(productId))
        );
    }
}