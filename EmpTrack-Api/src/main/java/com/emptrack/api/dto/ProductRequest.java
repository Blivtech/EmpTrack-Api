package com.emptrack.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {
    private String btCode;
    private String companyCode;
    private String productName;
    private String description;
    private List<WorkTypeRequest> workTypes;
}
