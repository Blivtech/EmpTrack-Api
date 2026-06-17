package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MonthlyEmployeeDetailResponse {
    private String empCode;
    private String empName;
    private String department;
    private String designation;
    private String deptName;
    private String desgName;
    private String shiftCode;
    private String shiftName;
    private String month;
    private int    presentDays;
    private int    absentDays;
    private int    holidayDays;
    private int    weekOffDays;
    private int    totalDays;
    private int    attendancePercent;
    private List<String> presentDates;      // ["2026-06-01", ...]
    private List<String> absentDates;
    private List<String> holidayDates;
    private List<String> weekOffDates;
}