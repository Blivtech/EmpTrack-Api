package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyShiftEmployeeResponse {
    private String empCode;
    private String empName;
    private String department;
    private String designation;
    private int    presentDays;
    private int    absentDays;
    private int    totalDays;
    private int    attendancePercent;
}
