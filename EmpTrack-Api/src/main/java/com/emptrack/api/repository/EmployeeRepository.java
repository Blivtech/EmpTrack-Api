package com.emptrack.api.repository;

import com.emptrack.api.model.TblEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<TblEmployee, Long> {

    boolean existsByPhoneAndBtCode(String phone, String btCode);
    boolean existsByEmailAndBtCode(String email, String btCode);
    Optional<TblEmployee> findTopByOrderByIdDesc();

    TblEmployee  findByEmpCode(String empCode);
    List<TblEmployee> findByBtCodeAndStatus(String btCode, Integer status);

}