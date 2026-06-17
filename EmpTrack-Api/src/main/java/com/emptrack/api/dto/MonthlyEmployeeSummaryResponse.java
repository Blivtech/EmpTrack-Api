package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyEmployeeSummaryResponse {
    private String empCode;
    private String empName;
    private String department;
    private String designation;
    private String deptName;
    private String desgName;
    private String shiftCode;
    private String shiftName;
    private int    presentDays;
    private int    absentDays;
    private int    holidayDays;
    private int    weekOffDays;
    private int    totalDays;
    private int    attendancePercent;
}
