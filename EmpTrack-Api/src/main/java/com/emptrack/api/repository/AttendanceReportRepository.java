package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceReportRepository
        extends JpaRepository<TblAttendance, Long> {

    // ✅ Get all shift headers for a date
    List<TblAttendance> findByBtCodeAndCompanyCodeAndAttendanceDateOrderByCreatedAtAsc(
            String btCode, String companyCode, LocalDate attendanceDate
    );

    // ✅ Get single shift header
    Optional<TblAttendance> findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCode(
            String btCode, String companyCode,
            LocalDate attendanceDate, String shiftCode
    );
}