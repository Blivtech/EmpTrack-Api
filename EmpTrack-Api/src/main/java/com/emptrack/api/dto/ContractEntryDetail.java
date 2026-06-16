package com.emptrack.api.dto;

import lombok.Data;

@Data
public class ContractEntryDetail {
    private String productId;
    private String productName;
    private String workName;
    private Double quantityDone;
    private Double ratePerUnit;
    private Double totalAmount;
    private String unit;
}
