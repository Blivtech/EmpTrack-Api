package com.emptrack.api.repository;

import com.emptrack.api.model.TblCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<TblCompany, Long> {

    boolean existsByNameAndBtCode(String name, String btCode);
    boolean existsByCompanyCodeAndBtCode(String companyCode, String btCode);
    boolean existsByNameAndBtCodeAndCompanyCodeNot(String name, String btCode, String companyCode);
    Optional<TblCompany> findTopByOrderByIdDesc();
    TblCompany  findByCompanyCode(String companyCode);
    List<TblCompany> findByBtCodeAndStatus(String btCode, Integer status);
}