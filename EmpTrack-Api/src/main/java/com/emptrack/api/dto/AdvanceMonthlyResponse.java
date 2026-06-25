// AdvanceMonthlyResponse.java
package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdvanceMonthlyResponse {
    private String                    month;
    private int                       totalEntries;
    private double                    totalAmount;
    private List<AdvanceEntryResponse> entries;
}