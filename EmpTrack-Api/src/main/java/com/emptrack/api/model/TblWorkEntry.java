package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_work_entry")
public class TblWorkEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_id", unique = true)
    private String entryId;

    @Column(name = "bt_code")
    private String btCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "emp_code")
    private String empCode;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "work_type_id")
    private String workTypeId;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "pieces_done")
    private Double piecesDone;

    @Column(name = "rate_per_piece")
    private Double ratePerPiece;

    @Column(name = "total_amount")
    private Double totalAmount;

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