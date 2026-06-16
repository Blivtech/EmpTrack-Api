package com.emptrack.api.dto;

import lombok.Data;

@Data
public class UpdateContractRateRequest {
    private Double newRate;
    private String changedBy;
    private String reason;
    private String effectiveDate;   // yyyy-MM-dd
}