package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CalendarDayResponse {
    private LocalDate date;
    private Boolean hasMissing;
    private List<CalendarShiftResponse> shifts;
}