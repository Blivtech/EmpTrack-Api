package com.emptrack.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkTypeResponse {
    private String workTypeId;
    private String productId;
    private String workTypeName;
    private Double ratePerPiece;
    private String unit;
    private String colorTag;
    private Integer status;
}