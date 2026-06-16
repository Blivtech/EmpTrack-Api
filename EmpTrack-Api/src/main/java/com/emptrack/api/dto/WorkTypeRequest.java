package com.emptrack.api.dto;

import lombok.Data;

@Data
public class WorkTypeRequest {
    private String workTypeName;
    private Double ratePerPiece;
    private String unit;
    private String colorTag;
}
