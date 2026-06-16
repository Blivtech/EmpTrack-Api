package com.emptrack.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class ContractEntryRequest {
    private String btCode;
    private String companyCode;
    private String shiftCode;
    private String shiftName;
    private String entryDate;       // yyyy-MM-dd
    private List<ContractEntryDetail> entries;
}
