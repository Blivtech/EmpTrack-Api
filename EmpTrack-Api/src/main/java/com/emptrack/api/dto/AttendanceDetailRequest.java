package com.emptrack.api.dto;

import lombok.Data;

@Data
public class AttendanceDetailRequest {
    private String empCode;
    private Integer dayPlanStatus; // 1=Working 2=WeekOff 3=Leave 4=Holiday
    private Integer workType;      // 1=Full 2=Half
    private Double presentCount;
    private Integer absentCount;
    private String remarks;
}