package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractEntryResponse {
    private String entryId;
    private String btCode;
    private String companyCode;
    private String shiftCode;
    private String shiftName;
    private String entryDate;
    private String productId;
    private String productName;
    private String workName;
    private Double quantityDone;
    private Double ratePerUnit;
    private Double totalAmount;
    private String unit;
    private Integer status;
    private String createdAt;
}