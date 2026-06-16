package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkEntryResponse {
    private String entryId;
    private String btCode;
    private String companyCode;
    private String empCode;
    private String empName;
    private String productId;
    private String productName;
    private String workTypeId;
    private String workTypeName;
    private String entryDate;
    private Double piecesDone;
    private Double ratePerPiece;
    private Double totalAmount;
    private String remarks;
    private Integer status;
    private String createdAt;
}