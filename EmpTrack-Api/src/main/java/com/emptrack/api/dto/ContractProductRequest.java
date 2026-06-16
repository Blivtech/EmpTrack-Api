package com.emptrack.api.dto;

import lombok.Data;

@Data
public class ContractProductRequest {
    private String btCode;
    private String companyCode;
    private String productName;
    private String workName;
    private Double ratePerUnit;
    private String unit;
    private String colorTag;
}