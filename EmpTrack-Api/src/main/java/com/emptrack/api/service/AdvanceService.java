package com.emptrack.api.service;

import com.emptrack.api.dto.AdvanceRequest;
import com.emptrack.api.model.TblAdvance;
import com.emptrack.api.repository.AdvanceRepository;
import com.emptrack.api.repository.CompanyRepository;
import com.emptrack.api.repository.EmployeeRepository;
import com.emptrack.api.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdvanceService {

    @Autowired
    private AdvanceRepository advanceRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // ── Save Multiple Advances ─────────────────────────────
    @Transactional
    public ApiResponse<?> saveAdvances(AdvanceRequest request) {

        // ── Validations ────────────────────────────────────
        if (request.getBtCode() == null || request.getBtCode().isEmpty())
            return ApiResponse.error(400, "bt_code is required");

        if (request.getCompanyCode() == null || request.getCompanyCode().isEmpty())
            return ApiResponse.error(400, "company_code is required");

        if (request.getAdvances() == null || request.getAdvances().isEmpty())
            return ApiResponse.error(400, "At least one advance record is required");

        // ── Company Existence Check ────────────────────────
        if (!companyRepository.existsByCompanyCodeAndBtCode(request.getCompanyCode(),request.getBtCode()))
            return ApiResponse.error(404, "Company not found");

        // ── Process Each Advance ───────────────────────────
        List<TblAdvance> savedList    = new ArrayList<>();
        List<String>     failedList   = new ArrayList<>();

        for (AdvanceRequest.AdvanceItem item : request.getAdvances()) {

            // Item level validations
            if (item.getEmpCode() == null || item.getEmpCode().isEmpty()) {
                failedList.add("emp_code is missing in one record");
                continue;
            }

            if (item.getAdvanceDate() == null) {
                failedList.add(item.getEmpCode() + " : advance date is required");
                continue;
            }

            if (item.getAmount() == null || item.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                failedList.add(item.getEmpCode() + " : amount must be greater than 0");
                continue;
            }

            // Employee existence check
//            if (!employeeRepository.existsByEmpCode(item.getEmpCode())) {
//                failedList.add(item.getEmpCode() + " : employee not found");
//                continue;
//            }

            // Save
            TblAdvance advance = new TblAdvance();
            advance.setBtCode(request.getBtCode());
            advance.setCompanyCode(request.getCompanyCode());
            advance.setEmpCode(item.getEmpCode());
            advance.setAdvanceDate(item.getAdvanceDate());
            advance.setAmount(item.getAmount());
            advance.setReason(item.getReason());
            advance.setStatus(1);
            savedList.add(advanceRepository.save(advance));
        }

        // ── Build Response ─────────────────────────────────
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("saved",  savedList);
        data.put("failed", failedList);
        data.put("totalSaved",  savedList.size());
        data.put("totalFailed", failedList.size());

        String message = savedList.size() + " advance(s) saved successfully";
        if (!failedList.isEmpty()) {
            message += ", " + failedList.size() + " failed";
        }

        return ApiResponse.success(message, data);
    }

    // ── Update Advance ─────────────────────────────────────
    public ApiResponse<?> updateAdvance(Long id, AdvanceRequest.AdvanceItem request) {

        TblAdvance advance = advanceRepository.findById(id).orElse(null);
        if (advance == null)
            return ApiResponse.error(404, "Advance record not found");

        if (request.getAdvanceDate() != null)
            advance.setAdvanceDate(request.getAdvanceDate());

        if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0)
            advance.setAmount(request.getAmount());

        if (request.getReason() != null)
            advance.setReason(request.getReason());

        return ApiResponse.success("Advance updated successfully", advanceRepository.save(advance));
    }

    // ── Delete Advance ─────────────────────────────────────
    public ApiResponse<?> deleteAdvance(Long id) {

        TblAdvance advance = advanceRepository.findById(id).orElse(null);
        if (advance == null)
            return ApiResponse.error(404, "Advance record not found");

        advance.setStatus(0);
        advanceRepository.save(advance);
        return ApiResponse.success("Advance deleted successfully", null);
    }

    // ── Get by Employee ────────────────────────────────────
    public ApiResponse<?> getByEmployee(String empCode) {

        if (empCode == null || empCode.isEmpty())
            return ApiResponse.error(400, "emp_code is required");

        return ApiResponse.success("Advance fetched successfully",
                advanceRepository.findByEmpCodeAndStatus(empCode, 1));
    }

    // ── Get by Company ─────────────────────────────────────
    public ApiResponse<?> getByCompany(String companyCode, String btCode) {

        if (btCode == null || btCode.isEmpty())
            return ApiResponse.error(400, "bt_code is required");

        if (companyCode == null || companyCode.isEmpty())
            return ApiResponse.error(400, "company_code is required");

        return ApiResponse.success("Advance fetched successfully",
                advanceRepository.findByCompanyCodeAndBtCodeAndStatus(companyCode, btCode, 1));
    }
}