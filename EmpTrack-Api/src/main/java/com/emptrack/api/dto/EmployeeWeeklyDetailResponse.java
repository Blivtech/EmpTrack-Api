package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class EmployeeWeeklyDetailResponse {
    private String empCode;
    private String empName;
    private String department;
    private String designation;
    private String shiftCode;
    private String shiftName;
    private String weekStart;
    private String weekEnd;
    private int    presentDays;
    private int    absentDays;
    private int    lateDays;
    private int    holidayDays;
    private int    weekOffDays;
    private int    totalDays;
    private int    attendancePercent;
    private List<DailyStatusResponse> dailyStatus;
}