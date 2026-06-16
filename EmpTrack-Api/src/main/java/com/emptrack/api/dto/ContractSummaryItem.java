package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractSummaryItem {
    private String productId;
    private String productName;
    private String workName;
    private Double totalQty;
    private Double ratePerUnit;
    private Double totalAmount;
    private String unit;}
