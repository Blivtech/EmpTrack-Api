package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceDetailResponse {
    private String detailId;
    private String empCode;
    private String name;
    private Integer dayPlanStatus;
    private Integer workType;
    private Double presentCount;
    private Integer absentCount;
    private String remarks;
}