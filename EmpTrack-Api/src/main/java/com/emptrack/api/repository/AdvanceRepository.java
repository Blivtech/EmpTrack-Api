package com.emptrack.api.repository;

import com.emptrack.api.model.TblAdvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvanceRepository extends JpaRepository<TblAdvance, Long> {

    Optional<TblAdvance> findByAdvanceId(String advanceId);

    // ✅ Get all by company
    List<TblAdvance> findByBtCodeAndCompanyCodeOrderByRequestDateDesc(
        String btCode, String companyCode
    );

    // ✅ Get by month
    @Query("""
        SELECT a FROM TblAdvance a
        WHERE a.btCode = :btCode
        AND a.companyCode = :companyCode
        AND FUNCTION('DATE_FORMAT', a.requestDate, '%Y-%m') = :month
        ORDER BY a.requestDate DESC
    """)
    List<TblAdvance> findByMonth(
        @Param("btCode") String btCode,
        @Param("companyCode") String companyCode,
        @Param("month") String month
    );

    // ✅ Get by emp
    List<TblAdvance> findByBtCodeAndCompanyCodeAndEmpCodeOrderByRequestDateDesc(
        String btCode, String companyCode, String empCode
    );

    // ✅ Get active advances
    List<TblAdvance> findByBtCodeAndCompanyCodeAndStatus(
        String btCode, String companyCode, Integer status
    );

    // ✅ Get by repay month
    List<TblAdvance> findByBtCodeAndCompanyCodeAndRepayMonth(
        String btCode, String companyCode, String repayMonth
    );
}