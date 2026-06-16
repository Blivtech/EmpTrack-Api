package com.emptrack.api.repository;

import com.emptrack.api.model.TblWorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkTypeRepository extends JpaRepository<TblWorkType, Long> {

    Optional<TblWorkType> findByWorkTypeId(String workTypeId);

    List<TblWorkType> findByProductIdAndStatusOrderByCreatedAtAsc(
        String productId, Integer status
    );

    List<TblWorkType> findByProductIdOrderByCreatedAtAsc(String productId);

    void deleteByProductId(String productId);
    // ✅ Add this method
    List<TblWorkType> findByProductIdAndStatus(String productId, Integer status);
}