package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendanceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceDetailReportRepository
    extends JpaRepository<TblAttendanceDetail, Long> {

    // ✅ Get all employee details for a shift
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCode(
        String btCode, String companyCode,
        LocalDate attendanceDate, String shiftCode
    );

    // ✅ Get present employees — present_count > 0
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCodeAndPresentCountGreaterThan(
        String btCode, String companyCode,
        LocalDate attendanceDate, String shiftCode,
        Double presentCount
    );

    // ✅ Get absent employees — absent_count = 1
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCodeAndAbsentCount(
        String btCode, String companyCode,
        LocalDate attendanceDate, String shiftCode,
        Integer absentCount
    );

    // ✅ Holiday + WeekOff — dayPlanStatus IN (3, 4)
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCodeAndDayPlanStatusIn(
            String btCode, String companyCode,
            LocalDate attendanceDate, String shiftCode,
            List<Integer> dayPlanStatuses
    );
}