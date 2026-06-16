package com.emptrack.api.repository;

import com.emptrack.api.model.TblWorkEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkEntryRepository extends JpaRepository<TblWorkEntry, Long> {

    Optional<TblWorkEntry> findByEntryId(String entryId);

    // ✅ Get by date
    List<TblWorkEntry> findByBtCodeAndCompanyCodeAndEntryDateOrderByCreatedAtDesc(
        String btCode, String companyCode, LocalDate entryDate
    );

    // ✅ Get by month
    @Query("""
        SELECT e FROM TblWorkEntry e
        WHERE e.btCode = :btCode
        AND e.companyCode = :companyCode
        AND FUNCTION('DATE_FORMAT', e.entryDate, '%Y-%m') = :month
        ORDER BY e.entryDate DESC
    """)
    List<TblWorkEntry> findByMonth(
        @Param("btCode") String btCode,
        @Param("companyCode") String companyCode,
        @Param("month") String month
    );

    // ✅ Get by emp + month
    @Query("""
        SELECT e FROM TblWorkEntry e
        WHERE e.btCode = :btCode
        AND e.companyCode = :companyCode
        AND e.empCode = :empCode
        AND FUNCTION('DATE_FORMAT', e.entryDate, '%Y-%m') = :month
        ORDER BY e.entryDate DESC
    """)
    List<TblWorkEntry> findByEmpAndMonth(
        @Param("btCode") String btCode,
        @Param("companyCode") String companyCode,
        @Param("empCode") String empCode,
        @Param("month") String month
    );

    // ✅ Get by emp + date
    List<TblWorkEntry> findByBtCodeAndCompanyCodeAndEmpCodeAndEntryDate(
        String btCode, String companyCode,
        String empCode, LocalDate entryDate
    );
}