package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_work_type_rate_history")
public class TblWorkTypeRateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "history_id", unique = true)
    private String historyId;

    @Column(name = "bt_code")
    private String btCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "work_type_id")
    private String workTypeId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "work_type_name")
    private String workTypeName;

    @Column(name = "old_rate")
    private Double oldRate;

    @Column(name = "new_rate")
    private Double newRate;

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