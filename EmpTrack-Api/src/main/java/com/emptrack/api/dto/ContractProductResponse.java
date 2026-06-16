package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractProductResponse {
    private String productId;
    private String btCode;
    private String companyCode;
    private String productName;
    private String workName;
    private Double ratePerUnit;
    private String unit;
    private String colorTag;
    private Integer status;
    private String createdAt;
}
