package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_contract_rate_history")
public class TblContractRateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "history_id", unique = true)
    private String historyId;

    @Column(name = "bt_code")
    private String btCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "work_name")
    private String workName;

    @Column(name = "old_rate")
    private Double oldRate;

    @Column(name = "new_rate")
    private Double newRate;

    @Column(name = "unit")
    private String unit;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "reason")
    private String reason;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}