package com.emptrack.api.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceRequest {
    private String btCode;
    private String companyCode;
    private String shiftCode;
    private LocalDate attendanceDate;
    private Long markedBy;
    private List<AttendanceDetailRequest> employees;
}