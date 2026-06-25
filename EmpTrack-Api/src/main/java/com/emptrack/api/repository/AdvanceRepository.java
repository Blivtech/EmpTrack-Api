package com.emptrack.api.repository;

import com.emptrack.api.model.TblAdvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvanceRepository extends JpaRepository<TblAdvance, Long> {

    Optional<TblAdvance> findByAdvanceId(String advanceId);          // ← this was missing



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


    List<TblAdvance> findByBtCodeAndCompanyCodeAndRequestDateBetweenAndStatusOrderByRequestDateAsc(
            String btCode, String companyCode,
            LocalDate startDate, LocalDate endDate,
            Integer status
    );
}