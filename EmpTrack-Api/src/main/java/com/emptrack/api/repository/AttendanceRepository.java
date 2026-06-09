package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
    extends JpaRepository<TblAttendance, Long> {

    // ✅ Find by attendance_id
    Optional<TblAttendance> findByAttendanceId(String attendanceId);



    // ✅ Get all shifts for today
    List<TblAttendance> findByBtCodeAndCompanyCodeAndAttendanceDate(
        String btCode,
        String companyCode,
        LocalDate date
    );

    // ✅ Calendar view
    @Query("SELECT a FROM TblAttendance a " +
           "WHERE a.btCode = :btCode " +
           "AND a.companyCode = :companyCode " +
           "AND MONTH(a.attendanceDate) = :month " +
           "AND YEAR(a.attendanceDate) = :year")
    List<TblAttendance> findByMonthAndYear(
        @Param("btCode") String btCode,
        @Param("companyCode") String companyCode,
        @Param("month") int month,
        @Param("year") int year
    );
}