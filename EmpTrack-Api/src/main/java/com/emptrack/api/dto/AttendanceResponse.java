package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AttendanceResponse {
    private String attendanceId;
    private String companyCode;
    private String shiftCode;
    private LocalDate attendanceDate;
    private Integer totalEmployees;
    private Double presentCount;
    private Integer absentCount;
    private Integer weekoffCount;
    private Integer leaveCount;
    private Integer holidayCount;
    private Boolean isMarked;
    private String mode;           // "NEW" or "EDIT"
    private List<AttendanceDetailResponse> employees;
}