// BonusEntryResponse.java
package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BonusEntryResponse {
    private Long   id;
    private String bonusId;
    private String btCode;
    private String companyCode;
    private String empCode;
    private String empName;
    private String deptName;
    private String bonusDate;
    private String bonusType;
    private Double amount;
    private String remarks;
    private Integer status;
}