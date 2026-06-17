package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendanceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeeklyReportRepository
    extends JpaRepository<TblAttendanceDetail, Long> {

    // ✅ All details for btCode + company + week range
    List<TblAttendanceDetail>
    findByBtCodeAndCompanyCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
        String btCode, String companyCode,
        LocalDate startDate, LocalDate endDate
    );

    // ✅ All details for shift + week range
    List<TblAttendanceDetail>
    findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
        String btCode, String companyCode,
        String shiftCode,
        LocalDate startDate, LocalDate endDate
    );

    // ✅ All details for employee + shift + week range
    List<TblAttendanceDetail>
    findByBtCodeAndCompanyCodeAndEmpCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
        String btCode, String companyCode,
        String empCode, String shiftCode,
        LocalDate startDate, LocalDate endDate
    );
}