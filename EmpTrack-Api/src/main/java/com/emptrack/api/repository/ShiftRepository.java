package com.emptrack.api.repository;

import com.emptrack.api.model.TblShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<TblShift, Long> {
    Optional<TblShift> findTopByOrderByIdDesc();
    List<TblShift> findByCompanyCodeAndStatus(String companyCode, Integer status);
    List<TblShift> findByBtCodeAndCompanyCode(String btCode,String companyCode);
    TblShift findByShiftCode(String shiftCode);
    void deleteByCompanyCode(String companyCode);
}