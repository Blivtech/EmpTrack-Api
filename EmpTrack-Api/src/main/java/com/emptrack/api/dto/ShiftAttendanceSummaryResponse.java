package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShiftAttendanceSummaryResponse {
    private String shiftCode;
    private String shiftName;
    private String startTime;
    private String endTime;
    private String attendanceDate;
    private String submittedAt;       // null = not submitted
    private int    presentCount;
    private int    leaveCount;
    private int    totalCount;
}