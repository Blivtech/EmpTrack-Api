package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyStatusResponse {
    private String date;
    private String dayName;
    private String status;
    private String statusLabel;
}
