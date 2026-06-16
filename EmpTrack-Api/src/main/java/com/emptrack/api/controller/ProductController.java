package com.emptrack.api.controller;


import com.emptrack.api.dto.ProductRequest;
import com.emptrack.api.dto.ProductResponse;
import com.emptrack.api.dto.RateHistoryResponse;
import com.emptrack.api.dto.UpdateRateRequest;
import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ✅ Add product
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(
        @RequestBody ProductRequest req
    ) {
        productService.addProduct(req);
        return ResponseEntity.ok(ApiResponse.success("Product saved successfully","[]"));
    }

    // ✅ Get all products
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
        @RequestParam String btCode,
        @RequestParam String companyCode
    ) {
        List<ProductResponse> list = productService.getProducts(btCode, companyCode);
        return ResponseEntity.ok(ApiResponse.success("",list));
    }

    // ✅ Update product
    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<String>> update(
        @PathVariable String productId,
        @RequestBody ProductRequest req
    ) {
        productService.updateProduct(productId, req);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully","[]"));
    }

    // ✅ Delete product
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> delete(
        @PathVariable String productId
    ) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully","[]"));
    }

    // ✅ Update rate
    @PutMapping("/rate/{workTypeId}")
    public ResponseEntity<ApiResponse<String>> updateRate(
        @PathVariable String workTypeId,
        @RequestBody UpdateRateRequest req
    ) {
        productService.updateRate(workTypeId, req);
        return ResponseEntity.ok(ApiResponse.success("Rate updated successfully","[]"));
    }

    // ✅ Get rate history
    @GetMapping("/history/{workTypeId}")
    public ResponseEntity<ApiResponse<List<RateHistoryResponse>>> getRateHistory(
        @PathVariable String workTypeId
    ) {
        List<RateHistoryResponse> list = productService.getRateHistory(workTypeId);
        return ResponseEntity.ok(ApiResponse.success("",list));
    }
}