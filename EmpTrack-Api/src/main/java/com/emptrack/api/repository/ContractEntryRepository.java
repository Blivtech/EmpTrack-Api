package com.emptrack.api.repository;


import com.emptrack.api.model.TblContractEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractEntryRepository
    extends JpaRepository<TblContractEntry, Long> {

    Optional<TblContractEntry> findByEntryId(String entryId);

    // ✅ Get by date + shift
    List<TblContractEntry> findByBtCodeAndCompanyCodeAndEntryDateAndShiftCode(
        String btCode, String companyCode,
        LocalDate entryDate, String shiftCode
    );

    // ✅ Get by month
    @Query("""
        SELECT e FROM TblContractEntry e
        WHERE e.btCode = :btCode
        AND e.companyCode = :companyCode
        AND FUNCTION('DATE_FORMAT', e.entryDate, '%Y-%m') = :month
        ORDER BY e.entryDate DESC, e.createdAt DESC
    """)
    List<TblContractEntry> findByMonth(
        @Param("btCode") String btCode,
        @Param("companyCode") String companyCode,
        @Param("month") String month
    );

    // ✅ Get by date range
    List<TblContractEntry> findByBtCodeAndCompanyCodeAndEntryDateBetweenOrderByEntryDateDesc(
        String btCode, String companyCode,
        LocalDate startDate, LocalDate endDate
    );
}