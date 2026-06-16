package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyReportRepository
    extends JpaRepository<TblAttendance, Long> {

    // ✅ Get all headers for a date range + shift
    List<TblAttendance> findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
        String btCode, String companyCode,
        String shiftCode,
        LocalDate weekStart, LocalDate weekEnd
    );

    // ✅ Get single header
    Optional<TblAttendance> findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDate(
        String btCode, String companyCode,
        String shiftCode, LocalDate attendanceDate
    );

    // ✅ Get all headers for date range (all shifts)
    List<TblAttendance> findByBtCodeAndCompanyCodeAndAttendanceDateBetween(
        String btCode, String companyCode,
        LocalDate weekStart, LocalDate weekEnd
    );
}