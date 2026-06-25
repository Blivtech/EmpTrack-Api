// OvertimeMonthlyResponse.java
package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class OvertimeMonthlyResponse {
    private String                      month;
    private int                         totalEntries;
    private double                      totalHours;
    private double                      totalAmount;
    private List<OvertimeEntryResponse> entries;
}