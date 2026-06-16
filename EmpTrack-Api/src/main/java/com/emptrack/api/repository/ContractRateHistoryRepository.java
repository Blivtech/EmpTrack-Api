package com.emptrack.api.repository;


import com.emptrack.api.model.TblContractRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractRateHistoryRepository
    extends JpaRepository<TblContractRateHistory, Long> {

    List<TblContractRateHistory> findByProductIdOrderByCreatedAtDesc(
        String productId
    );
}