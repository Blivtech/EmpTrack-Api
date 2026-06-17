package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WeeklyOverallReportResponse {
    private String btCode;
    private String companyCode;
    private String weekStart;
    private String weekEnd;
    private int    totalEmployees;
    private int    totalPresent;
    private int    totalAbsent;
    private int    totalHoliday;
    private int    totalWeekOff;
    private int    workDays;
    private List<WeeklyEmployeeSummaryResponse> employees;
}