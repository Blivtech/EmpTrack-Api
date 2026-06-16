package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyShiftSummaryResponse {
    private String shiftCode;
    private String shiftName;
    private String startTime;
    private String endTime;
    private int    totalEmployees;
    private int    totalPresent;
    private int    totalAbsent;
    private int    submittedDays;
    private int    totalDays;
}
