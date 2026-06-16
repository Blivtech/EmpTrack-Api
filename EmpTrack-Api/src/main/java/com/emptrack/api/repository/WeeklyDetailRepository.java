
// ─── WeeklyDetailRepository.java ──────────────────
package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendanceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyDetailRepository
    extends JpaRepository<TblAttendanceDetail, Long> {

    // ✅ Get all details for emp + shift + date range
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndEmpCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
        String btCode, String companyCode,
        String empCode, String shiftCode,
        LocalDate weekStart, LocalDate weekEnd
    );

    // ✅ Get all details for shift + date range
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetween(
        String btCode, String companyCode,
        String shiftCode,
        LocalDate weekStart, LocalDate weekEnd
    );

    // ✅ Get distinct emp codes for shift + date range
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenAndPresentCountGreaterThan(
        String btCode, String companyCode,
        String shiftCode,
        LocalDate weekStart, LocalDate weekEnd,
        Double presentCount
    );

    // ✅ Absent employees
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenAndAbsentCount(
        String btCode, String companyCode,
        String shiftCode,
        LocalDate weekStart, LocalDate weekEnd,
        Integer absentCount
    );
}