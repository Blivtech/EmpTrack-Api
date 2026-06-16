package com.emptrack.api.dto;

import lombok.Data;
import lombok.Builder;

// ─── Overtime ───────────────────────
@Data
@Builder
public class OvertimeResponse {
    private String  otId;
    private String  btCode;
    private String  companyCode;
    private String  empCode;
    private String  empName;
    private String  otDate;
    private String  shiftCode;
    private String  shiftName;
    private Double  otHours;
    private Double  otAmount;
    private String  remarks;
    private Integer status;
    private String  statusLabel;
    private String  createdAt;
}

