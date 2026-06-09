package com.emptrack.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class CompanyRequest {

    private String btCode;
    private String name;
    private String address;
    private String city;
    private String state;
    private String phone;
    private String email;
    private String logo;
    private List<ShiftRequest> shifts;

    @Data
    public static class ShiftRequest {
        private String shiftName;
        private String startTime;
        private String endTime;
    }
}