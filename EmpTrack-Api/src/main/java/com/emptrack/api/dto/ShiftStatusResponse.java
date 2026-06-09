package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShiftStatusResponse {
    private String shiftId;
    private String shiftName;
    private String attendanceId;   // null if not marked
    private Boolean isMarked;
    private Double presentCount;
    private Integer absentCount;
    private Integer weekoffCount;
    private Integer leaveCount;
    private Integer holidayCount;
    private String mode;           // "NEW" or "EDIT"
}