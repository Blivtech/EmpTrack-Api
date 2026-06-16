package com.emptrack.api.dto;

import lombok.Data;

// ─── Overtime ───────────────────────
@Data
public class OvertimeRequest {
    private String btCode;
    private String companyCode;
    private String empCode;
    private String otDate;        // yyyy-MM-dd
    private String shiftCode;
    private Double otHours;
    private Double otAmount;
    private String remarks;
}

