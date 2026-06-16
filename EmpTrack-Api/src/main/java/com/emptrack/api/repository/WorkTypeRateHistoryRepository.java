package com.emptrack.api.repository;

import com.emptrack.api.model.TblWorkTypeRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkTypeRateHistoryRepository
    extends JpaRepository<TblWorkTypeRateHistory, Long> {

    List<TblWorkTypeRateHistory> findByWorkTypeIdOrderByCreatedAtDesc(
        String workTypeId
    );

    List<TblWorkTypeRateHistory> findByBtCodeAndCompanyCodeOrderByCreatedAtDesc(
        String btCode, String companyCode
    );
}