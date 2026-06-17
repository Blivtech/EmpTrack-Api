package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendanceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MonthlyReportRepository
    extends JpaRepository<TblAttendanceDetail, Long> {

    // ✅ All details for btCode + company + month range
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
        String btCode, String companyCode,
        LocalDate startDate, LocalDate endDate
    );

    // ✅ All details for shift + month range
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
        String btCode, String companyCode,
        String shiftCode,
        LocalDate startDate, LocalDate endDate
    );

    // ✅ All details for single employee + shift + month range
    List<TblAttendanceDetail> findByBtCodeAndCompanyCodeAndEmpCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
        String btCode, String companyCode,
        String empCode, String shiftCode,
        LocalDate startDate, LocalDate endDate
    );

    // ✅ Distinct empCodes for company + month
    @Query("SELECT DISTINCT d.empCode FROM TblAttendanceDetail d " +
           "WHERE d.btCode = :btCode " +
           "AND d.companyCode = :companyCode " +
           "AND d.attendanceDate BETWEEN :startDate AND :endDate")
    List<String> findDistinctEmpCodesByBtCodeAndCompanyCodeAndDateRange(
        @Param("btCode")      String btCode,
        @Param("companyCode") String companyCode,
        @Param("startDate")   LocalDate startDate,
        @Param("endDate")     LocalDate endDate
    );

    // ✅ Distinct empCodes for shift + month
    @Query("SELECT DISTINCT d.empCode FROM TblAttendanceDetail d " +
           "WHERE d.btCode = :btCode " +
           "AND d.companyCode = :companyCode " +
           "AND d.shiftCode = :shiftCode " +
           "AND d.attendanceDate BETWEEN :startDate AND :endDate")
    List<String> findDistinctEmpCodesByShiftAndDateRange(
        @Param("btCode")      String btCode,
        @Param("companyCode") String companyCode,
        @Param("shiftCode")   String shiftCode,
        @Param("startDate")   LocalDate startDate,
        @Param("endDate")     LocalDate endDate
    );
}