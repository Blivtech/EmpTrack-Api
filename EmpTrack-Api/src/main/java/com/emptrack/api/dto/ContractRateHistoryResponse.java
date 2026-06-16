package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractRateHistoryResponse {
    private String historyId;
    private String productId;
    private String productName;
    private String workName;
    private Double oldRate;
    private Double newRate;
    private String unit;
    private String changedBy;
    private String reason;
    private String effectiveDate;
    private String createdAt;
}