package com.emptrack.api.service;

import com.emptrack.api.model.TblEmployee;
import com.emptrack.api.repository.CompanyRepository;
import com.emptrack.api.repository.DepartmentRepository;
import com.emptrack.api.repository.DesignationRepository;
import com.emptrack.api.repository.EmployeeRepository;
import com.emptrack.api.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    // Auto generate emp_code → EMP001, EMP002 ...
    private String generateEmpCode() {
        return employeeRepository.findTopByOrderByIdDesc()
                .map(e -> {
                    String last = e.getEmpCode().replace("EMP", "");
                    int next = Integer.parseInt(last) + 1;
                    return String.format("EMP%03d", next);
                })
                .orElse("EMP001");
    }

    public ApiResponse<?> saveEmployee(TblEmployee request) {

        // ── Mandatory Validations ──────────────────────────
        if (request.getBtCode() == null || request.getBtCode().isEmpty()) {
            return ApiResponse.error(400, "bt_code is required");
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            return ApiResponse.error(400, "Employee name is required");
        }

        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            return ApiResponse.error(400, "Phone number is required");
        }

        if (request.getCompanyCode() == null) {
            return ApiResponse.error(400, "Company is required");
        }

        if (request.getDeptCode() == null) {
            return ApiResponse.error(400, "Department is required");
        }

        if (request.getDesgCode() == null) {
            return ApiResponse.error(400, "Designation is required");
        }

        if (request.getSalaryType() == null) {
            return ApiResponse.error(400, "Salary type is required");
        }

        if (request.getSalaryAmount() == null || request.getSalaryAmount() <= 0) {
            return ApiResponse.error(400, "Salary amount is required");
        }

        // ── Existence Validations ──────────────────────────
//        if (!companyRepository.existsByCompanyCode(request.getCompanyCode())) {
//            return ApiResponse.error(404, "Company not found");
//        }

        if (!departmentRepository.existsByDeptCode(request.getDeptCode())) {
            return ApiResponse.error(404, "Department not found");
        }

        if (!designationRepository.existsByDesgCode(request.getDesgCode())) {
            return ApiResponse.error(404, "Designation not found");
        }

        // ── Duplicate Validations ──────────────────────────
        if (employeeRepository.existsByPhoneAndBtCode(request.getPhone(), request.getBtCode())) {
            return ApiResponse.error(409, "Phone number already exists");
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (employeeRepository.existsByEmailAndBtCode(request.getEmail(), request.getBtCode())) {
                return ApiResponse.error(409, "Email already exists");
            }
        }

        // ── Save ───────────────────────────────────────────
        request.setEmpCode(generateEmpCode());
        request.setStatus(1);
        TblEmployee saved = employeeRepository.save(request);

        return ApiResponse.success("Employee saved successfully", saved);
    }

    public ApiResponse<?> updateEmployee(String empCode,String companyCode, TblEmployee request) {

        // ── Find existing ──────────────────────────────────
// ── Find existing ──────────────────────────────────
        TblEmployee existing = employeeRepository
                .findByEmpCodeAndCompanyCode(empCode, companyCode)
                .orElse(null);
        if (existing == null) {
            return ApiResponse.error(404, "Employee not found: " + empCode);
        }

        // ── Mandatory field validations ────────────────────
        if (request.getName() != null && request.getName().isEmpty()) {
            return ApiResponse.error(400, "Employee name cannot be empty");
        }

        if (request.getPhone() != null && request.getPhone().isEmpty()) {
            return ApiResponse.error(400, "Phone number cannot be empty");
        }

        if (request.getSalaryAmount() != null && request.getSalaryAmount() <= 0) {
            return ApiResponse.error(400, "Salary amount must be greater than 0");
        }

        // ── Existence validations ──────────────────────────
        if (request.getDeptCode() != null) {
            if (!departmentRepository.existsByDeptCode(request.getDeptCode())) {
                return ApiResponse.error(404, "Department not found");
            }
        }

        if (request.getDesgCode() != null) {
            if (!designationRepository.existsByDesgCode(request.getDesgCode())) {
                return ApiResponse.error(404, "Designation not found");
            }
        }

        // ── Duplicate validations ──────────────────────────
        // ✅ Phone — exclude current employee
        if (request.getPhone() != null &&
                !request.getPhone().equals(existing.getPhone())) {
            if (employeeRepository.existsByPhoneAndBtCode(
                    request.getPhone(), existing.getBtCode()
            )) {
                return ApiResponse.error(409, "Phone number already exists");
            }
        }

        // ✅ Email — exclude current employee
        if (request.getEmail() != null &&
                !request.getEmail().isEmpty() &&
                !request.getEmail().equals(existing.getEmail())) {
            if (employeeRepository.existsByEmailAndBtCode(
                    request.getEmail(), existing.getBtCode()
            )) {
                return ApiResponse.error(409, "Email already exists");
            }
        }

        // ── Update only non-null fields ────────────────────
        // ✅ Basic info
        if (request.getName()         != null) existing.setName(request.getName());
        if (request.getPhone()        != null) existing.setPhone(request.getPhone());
        if (request.getEmail()        != null) existing.setEmail(request.getEmail());
        if (request.getGender()       != null) existing.setGender(request.getGender());
        if (request.getDob()          != null) existing.setDob(request.getDob());
        if (request.getProfileImage() != null) existing.setProfileImage(request.getProfileImage());

        // ✅ Job info
        if (request.getCompanyCode()  != null) existing.setCompanyCode(request.getCompanyCode());
        if (request.getDeptCode()     != null) existing.setDeptCode(request.getDeptCode());
        if (request.getDesgCode()     != null) existing.setDesgCode(request.getDesgCode());
        if (request.getJoiningDate()  != null) existing.setJoiningDate(request.getJoiningDate());

        // ✅ Salary info
        if (request.getSalaryType()         != null) existing.setSalaryType(request.getSalaryType());
        if (request.getSalaryAmount()       != null) existing.setSalaryAmount(request.getSalaryAmount());
        if (request.getLastAppraisalDate()  != null) existing.setLastAppraisalDate(request.getLastAppraisalDate());

        // ✅ Status
        if (request.getStatus() != null) existing.setStatus(request.getStatus());

        // ── Save ───────────────────────────────────────────
        TblEmployee updated = employeeRepository.save(existing);

        return ApiResponse.success("Employee updated successfully", updated);
    }

    public ApiResponse<?> deleteEmployee(
            String empCode, String companyCode
    ) {

        // ── Find existing ──────────────────────────────────
        TblEmployee existing = employeeRepository
                .findByEmpCodeAndCompanyCode(empCode, companyCode)
                .orElse(null);

        if (existing == null) {
            return ApiResponse.error(404,
                    "Employee not found: " + empCode
            );
        }

        // ── Check already deleted ──────────────────────────
        if (existing.getStatus() == 0) {
            return ApiResponse.error(400,
                    "Employee already deleted"
            );
        }

        // ── Soft delete ────────────────────────────────────
        existing.setStatus(0);
        employeeRepository.save(existing);

        return ApiResponse.success(
                "Employee deleted successfully", null
        );
    }
}