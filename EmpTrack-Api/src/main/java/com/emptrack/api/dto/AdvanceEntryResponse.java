// AdvanceEntryResponse.java
package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdvanceEntryResponse {
    private Long   id;
    private String advanceId;
    private String btCode;
    private String companyCode;
    private String empCode;
    private String empName;
    private String deptName;
    private String requestDate;
    private Double amount;
    private String repayMonth;
    private String remarks;
    private Integer status;
}