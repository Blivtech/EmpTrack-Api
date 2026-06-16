package com.emptrack.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class WorkEntryRequest {
    private String btCode;
    private String companyCode;
    private String empCode;
    private String entryDate;       // yyyy-MM-dd
    private List<WorkEntryDetail> entries;
}
