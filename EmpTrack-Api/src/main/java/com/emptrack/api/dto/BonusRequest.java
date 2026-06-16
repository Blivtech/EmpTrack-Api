package com.emptrack.api.dto;

import lombok.Data;

@Data
public class BonusRequest {
    private String btCode;
    private String companyCode;
    private String empCode;
    private String bonusDate;     // yyyy-MM-dd
    private String bonusType;
    private Double amount;
    private String remarks;
}
