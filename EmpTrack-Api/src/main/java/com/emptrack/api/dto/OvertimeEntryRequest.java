// OvertimeEntryRequest.java
package com.emptrack.api.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OvertimeEntryRequest {
    private String    btCode;
    private String    companyCode;
    private String    empCode;
    private LocalDate otDate;
    private Double    otHours;
    private Double    otAmount;
    private String    remarks;
}