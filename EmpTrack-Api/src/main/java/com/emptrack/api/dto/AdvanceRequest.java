package com.emptrack.api.dto;

import lombok.Data;

@Data
public class AdvanceRequest {
    private String btCode;
    private String companyCode;
    private String empCode;
    private String requestDate;   // yyyy-MM-dd
    private Double amount;
    private String repayMonth;    // yyyy-MM
    private String remarks;
}
