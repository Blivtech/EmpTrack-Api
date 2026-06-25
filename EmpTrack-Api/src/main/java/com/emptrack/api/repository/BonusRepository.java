package com.emptrack.api.repository;

import com.emptrack.api.model.TblBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BonusRepository extends JpaRepository<TblBonus, Long> {

    Optional<TblBonus> findByBonusId(String bonusId);                // ← added

    List<TblBonus> findByBtCodeAndCompanyCodeOrderByBonusDateDesc(
            String btCode, String companyCode
    );

    List<TblBonus> findByBtCodeAndCompanyCodeAndEmpCodeOrderByBonusDateDesc(
            String btCode, String companyCode, String empCode
    );

    List<TblBonus> findByBtCodeAndCompanyCodeAndBonusType(
            String btCode, String companyCode, String bonusType
    );

    @Query("""
        SELECT b FROM TblBonus b
        WHERE b.btCode = :btCode
        AND b.companyCode = :companyCode
        AND FUNCTION('DATE_FORMAT', b.bonusDate, '%Y-%m') = :month
        ORDER BY b.bonusDate DESC
    """)
    List<TblBonus> findByMonth(
            @Param("btCode") String btCode,
            @Param("companyCode") String companyCode,
            @Param("month") String month
    );

    List<TblBonus> findByBtCodeAndCompanyCodeAndBonusDateBetweenAndStatusOrderByBonusDateAsc(
            String btCode, String companyCode,
            LocalDate startDate, LocalDate endDate,
            Integer status
    );
}