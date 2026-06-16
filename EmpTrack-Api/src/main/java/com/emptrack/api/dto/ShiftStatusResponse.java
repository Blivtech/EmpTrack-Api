package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShiftStatusResponse {
    private String shiftCode;
    private String shiftName;
    private String attendanceId;
    private Boolean isMarked;
    private Double presentCount;
    private Integer absentCount;
    private Integer weekoffCount;
    private Integer leaveCount;
    private Integer holidayCount;
    private String mode;
}