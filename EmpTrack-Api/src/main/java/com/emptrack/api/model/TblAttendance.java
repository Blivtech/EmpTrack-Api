package com.emptrack.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_attendance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TblAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attendance_id", unique = true, nullable = false)
    private String attendanceId;

    @Column(name = "bt_code")
    private String btCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "shift_code")
    private String shiftCode;

    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @Column(name = "total_employees")
    private Integer totalEmployees;

    @Column(name = "present_count")
    private Double presentCount;

    @Column(name = "absent_count")
    private Integer absentCount;

    @Column(name = "weekoff_count")
    private Integer weekoffCount;

    @Column(name = "leave_count")
    private Integer leaveCount;

    @Column(name = "holiday_count")
    private Integer holidayCount;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}