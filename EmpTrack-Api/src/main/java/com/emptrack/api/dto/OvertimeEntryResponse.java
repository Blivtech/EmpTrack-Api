// OvertimeEntryResponse.java
package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OvertimeEntryResponse {
    private Long    id;
    private String  otId;
    private String  btCode;
    private String  companyCode;
    private String  empCode;
    private String  empName;
    private String  deptName;
    private String  otDate;
    private Double  otHours;
    private Double  ratePerHour;
    private Double  otAmount;
    private String  remarks;
    private Integer status;
}