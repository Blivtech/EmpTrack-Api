package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceEmployeeResponse {
    private String empCode;
    private String empName;
    private String desgCode;
    private String desgName;
    private String deptCode;
    private String deptName;
    private String status;            // P / A / L / H / WO
    private String statusLabel;       // Present / Absent / Late / Holiday
    private int    lateMinutes;
}