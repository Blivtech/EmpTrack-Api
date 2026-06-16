package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

// ─── Advance ────────────────────────
@Data
@Builder
public class AdvanceResponse {
    private String  advanceId;
    private String  btCode;
    private String  companyCode;
    private String  empCode;
    private String  empName;
    private String  requestDate;
    private Double  amount;
    private String  repayMonth;
    private String  remarks;
    private Integer status;
    private String  statusLabel;
    private String  createdAt;
}
