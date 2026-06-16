package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WeeklyReportResponse {
    private String btCode;
    private String companyCode;
    private String weekStart;
    private String weekEnd;
    private int    totalPresent;
    private int    totalAbsent;
    private int    workDays;
    private List<WeeklyShiftSummaryResponse> shifts;
}