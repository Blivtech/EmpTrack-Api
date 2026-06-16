package com.emptrack.api.repository;

import com.emptrack.api.model.TblProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<TblProduct, Long> {

    Optional<TblProduct> findByProductId(String productId);

    List<TblProduct> findByBtCodeAndCompanyCodeAndStatusOrderByCreatedAtDesc(
        String btCode, String companyCode, Integer status
    );

    List<TblProduct> findByBtCodeAndCompanyCodeOrderByCreatedAtDesc(
        String btCode, String companyCode
    );

    boolean existsByBtCodeAndCompanyCodeAndProductNameAndStatus(
        String btCode, String companyCode, String productName, Integer status
    );
    List<TblProduct> findByBtCodeAndStatus(String btCode, Integer status);

}