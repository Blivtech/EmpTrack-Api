package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarShiftResponse {
    private String shiftId;
    private String shiftName;
    private Boolean isMarked;
    private Double presentCount;
    private String status; // full/half/missing/off/leave/holiday
}