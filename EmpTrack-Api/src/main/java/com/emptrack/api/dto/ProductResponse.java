package com.emptrack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String productId;
    private String btCode;
    private String companyCode;
    private String productName;
    private String description;
    private Integer status;
    private String createdAt;
    private List<WorkTypeResponse> workTypes;
}