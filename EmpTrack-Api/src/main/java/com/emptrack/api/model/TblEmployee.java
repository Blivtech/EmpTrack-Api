package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_employees")
@Data
public class TblEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bt_code", nullable = false)
    private String btCode;

    @Column(name = "emp_code", unique = true, nullable = false)
    private String empCode;

    @Column(name = "company_code")
    private String companyCode;       // ✅ varchar(50)

    @Column(name = "dept_code")
    private String deptCode;          // ✅ varchar(50)

    @Column(name = "desg_code")
    private String desgCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "gender")
    private Integer gender;                 // 1=Male 2=Female 3=Other

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "salary_type")
    private Integer salaryType;             // 1=Daily 2=Weekly 3=Monthly

    @Column(name = "salary_amount")
    private Double salaryAmount;

    @Column(name = "last_appraisal_date")
    private LocalDate lastAppraisalDate;

    @Column(name = "status")
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}