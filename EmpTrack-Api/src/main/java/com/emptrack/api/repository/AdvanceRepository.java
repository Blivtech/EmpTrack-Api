package com.emptrack.api.repository;

import com.emptrack.api.model.TblAdvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvanceRepository extends JpaRepository<TblAdvance, Long> {
    List<TblAdvance> findByEmpCodeAndStatus(String empCode, Integer status);
    List<TblAdvance> findByCompanyCodeAndBtCodeAndStatus(String companyCode, String btCode, Integer status);
}