package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WorkSummaryResponse {
    private String empCode;
    private String empName;
    private Double totalPieces;
    private Double totalAmount;
    private List<WorkSummaryBreakdown> breakdown;
}
