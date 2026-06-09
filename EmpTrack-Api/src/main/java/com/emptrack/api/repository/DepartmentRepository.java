package com.emptrack.api.repository;

import com.emptrack.api.model.TblDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<TblDepartment, Long> {
    boolean existsByDeptCode(String deptCode);
    boolean existsByNameAndBtCode(String name, String btCode);
    Optional<TblDepartment> findTopByOrderByIdDesc();
    List<TblDepartment> findByBtCodeAndStatus(String btCode, Integer status);

}