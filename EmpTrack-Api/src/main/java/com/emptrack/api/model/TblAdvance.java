package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_advance")
public class TblAdvance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advance_id", unique = true)
    private String advanceId;

    @Column(name = "bt_code")
    private String btCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "emp_code")
    private String empCode;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "repay_month")
    private String repayMonth;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "status")
    private Integer status = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}