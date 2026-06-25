// AdvanceEntryRequest.java
package com.emptrack.api.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdvanceEntryRequest {
    private String    btCode;
    private String    companyCode;
    private String    empCode;
    private LocalDate requestDate;
    private Double    amount;
    private String    repayMonth;
    private String    remarks;
}