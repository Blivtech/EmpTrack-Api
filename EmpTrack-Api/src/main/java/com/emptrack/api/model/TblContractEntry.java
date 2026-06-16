package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_contract_entries")
public class TblContractEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_id", unique = true)
    private String entryId;

    @Column(name = "bt_code")
    private String btCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "shift_code")
    private String shiftCode;

    @Column(name = "shift_name")
    private String shiftName;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "work_name")
    private String workName;

    @Column(name = "quantity_done")
    private Double quantityDone;

    @Column(name = "rate_per_unit")
    private Double ratePerUnit;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "unit")
    private String unit;

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