package com.emptrack.api.dto;

import lombok.Data;

@Data
public class WorkEntryDetail {
    private String productId;
    private String workTypeId;
    private Double piecesDone;
    private Double ratePerPiece;
    private Double totalAmount;
    private String remarks;
}