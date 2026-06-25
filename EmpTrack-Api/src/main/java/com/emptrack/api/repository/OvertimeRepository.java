package com.emptrack.api.repository;

import com.emptrack.api.model.TblOvertime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OvertimeRepository extends JpaRepository<TblOvertime, Long> {

    Optional<TblOvertime> findByOtId(String otId);                   // ← added

    List<TblOvertime> findByBtCodeAndCompanyCodeOrderByOtDateDesc(
            String btCode, String companyCode
    );

    List<TblOvertime> findByBtCodeAndCompanyCodeAndEmpCodeOrderByOtDateDesc(
            String btCode, String companyCode, String empCode
    );

    boolean existsByBtCodeAndEmpCodeAndOtDateAndShiftCode(
            String btCode, String empCode,
            LocalDate otDate, String shiftCode
    );

    @Query("""
        SELECT o FROM TblOvertime o
        WHERE o.btCode = :btCode
        AND o.companyCode = :companyCode
        AND FUNCTION('DATE_FORMAT', o.otDate, '%Y-%m') = :month
        ORDER BY o.otDate DESC
    """)
    List<TblOvertime> findByMonth(
            @Param("btCode") String btCode,
            @Param("companyCode") String companyCode,
            @Param("month") String month
    );

    List<TblOvertime> findByBtCodeAndCompanyCodeAndOtDateBetweenAndStatusOrderByOtDateAsc(
            String btCode, String companyCode,
            LocalDate startDate, LocalDate endDate,
            Integer status
    );
}