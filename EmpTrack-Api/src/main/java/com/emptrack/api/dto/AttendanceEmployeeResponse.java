package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceEmployeeResponse {
    private String empCode;
    private String empName;
    private String department;
    private String designation;
    private String status;            // P / A / L / H / WO
    private String statusLabel;       // Present / Absent / Late / Holiday
    private int    lateMinutes;
}