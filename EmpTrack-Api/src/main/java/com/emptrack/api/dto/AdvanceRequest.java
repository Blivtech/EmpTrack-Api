package com.emptrack.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class AdvanceRequest {

    private String btCode;
    private String companyCode;
    private List<AdvanceItem> advances;

    @Data
    public static class AdvanceItem {
        private String     empCode;
        private LocalDate  advanceDate;
        private BigDecimal amount;
        private String     reason;
    }
}