package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WeeklyShiftReportResponse {
    private String shiftCode;
    private String shiftName;
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