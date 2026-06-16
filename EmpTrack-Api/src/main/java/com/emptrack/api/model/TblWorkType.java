package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_work_types")
public class TblWorkType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_type_id", unique = true)
    private String workTypeId;

    @Column(name = "bt_code")
    private String btCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "work_type_name")
    private String workTypeName;

    @Column(name = "rate_per_piece")
    private Double ratePerPiece;

    @Column(name = "unit")
    private String unit = "pieces";

    @Column(name = "color_tag")
    private String colorTag = "#1565C0";

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