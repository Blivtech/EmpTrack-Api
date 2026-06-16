package com.emptrack.api.repository;

import com.emptrack.api.model.TblContractProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractProductRepository
    extends JpaRepository<TblContractProduct, Long> {

    Optional<TblContractProduct> findByProductId(String productId);

    List<TblContractProduct> findByBtCodeAndCompanyCodeAndStatusOrderByCreatedAtDesc(
        String btCode, String companyCode, Integer status
    );

    List<TblContractProduct> findByBtCodeAndStatus(
        String btCode, Integer status
    );
}
