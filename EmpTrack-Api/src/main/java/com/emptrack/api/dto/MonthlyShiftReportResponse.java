package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MonthlyShiftReportResponse {
    private String shiftCode;
    private String shiftName;
    private String month;
    private int    totalEmployees;
    private int    totalPresent;
    private int    totalAbsent;
    private int    totalHoliday;
    private int    totalWeekOff;
    private int    workingDays;
    private List<MonthlyEmployeeSummaryResponse> employees;
}
