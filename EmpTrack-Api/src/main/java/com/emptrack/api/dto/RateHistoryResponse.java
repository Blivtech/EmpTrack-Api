package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateHistoryResponse {
    private String historyId;
    private String workTypeId;
    private String workTypeName;
    private Double oldRate;
    private Double newRate;
    private String changedBy;
    private String reason;
    private String effectiveDate;
    private String createdAt;
}