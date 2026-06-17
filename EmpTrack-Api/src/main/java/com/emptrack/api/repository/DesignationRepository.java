package com.emptrack.api.repository;

import com.emptrack.api.model.TblDesignation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignationRepository extends JpaRepository<TblDesignation, Long> {
    boolean existsByDesgCode(String desgCode);
    boolean existsByNameAndBtCode(String name, String btCode);
    Optional<TblDesignation> findTopByOrderByIdDesc();
    List<TblDesignation> findByBtCodeAndStatus(String btCode, Integer status);
    List<TblDesignation> findByDesgCodeIn(List<String> desgCodes);
    TblDesignation findByDesgCode(String desgCode);

}