package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ContractSummaryResponse {
    private String month;
    private String btCode;
    private String companyCode;
    private Double totalAmount;
    private List<ContractSummaryItem> breakdown;
}