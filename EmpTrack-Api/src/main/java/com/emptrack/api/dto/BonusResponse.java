package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

// ─── Bonus ──────────────────────────
@Data
@Builder
public class BonusResponse {
    private String  bonusId;
    private String  btCode;
    private String  companyCode;
    private String  empCode;
    private String  empName;
    private String  bonusDate;
    private String  bonusType;
    private Double  amount;
    private String  remarks;
    private Integer status;
    private String  statusLabel;
    private String  createdAt;
}
