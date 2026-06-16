package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkSummaryBreakdown {
    private String productId;
    private String productName;
    private String workTypeId;
    private String workTypeName;
    private Double totalPieces;
    private Double ratePerPiece;
    private Double totalAmount;
}