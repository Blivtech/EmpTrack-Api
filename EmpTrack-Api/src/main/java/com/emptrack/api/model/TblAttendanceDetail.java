package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_attendance_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TblAttendanceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "detail_id", unique = true, nullable = false)
    private String detailId;

    @Column(name = "attendance_id", nullable = false)
    private String attendanceId;

    @Column(name = "bt_code")
    private String btCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "shift_code")
    private String shiftCode;


    @Column(name = "emp_code")
    private String empCode;

    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @Column(name = "day_plan_status")
    private Integer dayPlanStatus;

    @Column(name = "work_type")
    private Integer workType;

    @Column(name = "present_count")
    private Double presentCount;

    @Column(name = "absent_count")
    private Integer absentCount;

    private String remarks;

    @Column(name = "marked_by")
    private Long markedBy;

    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = 1;
        if (workType == null) workType = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}