// BonusEntryRequest.java
package com.emptrack.api.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BonusEntryRequest {
    private String    btCode;
    private String    companyCode;
    private String    empCode;
    private LocalDate bonusDate;
    private String    bonusType;
    private Double    amount;
    private String    remarks;
}