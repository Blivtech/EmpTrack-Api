package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DailyReportSummaryResponse {
    private String btCode;
    private String companyCode;
    private String attendanceDate;
    private int    totalPresent;
    private int    totalLeave;
    private int    totalEmployees;
    private int    submittedShifts;
    private int    pendingShifts;
    private List<ShiftAttendanceSummaryResponse> shifts;
}