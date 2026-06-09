package com.emptrack.api.service;

import com.emptrack.api.dto.CompanyResponse;
import com.emptrack.api.model.*;
import com.emptrack.api.repository.*;
import com.emptrack.api.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MasterService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ShiftRepository shiftRepository;

    public ApiResponse<?> getMasterData(String btCode) {

        // ── Validation ─────────────────────────────────────
        if (btCode == null || btCode.trim().isEmpty()) {
            return ApiResponse.error(400, "bt_code is required");
        }

        List<TblDepartment>  departments  = departmentRepository.findByBtCodeAndStatus(btCode, 1);
        List<TblDesignation> designations = designationRepository.findByBtCodeAndStatus(btCode, 1);
        List<TblCompany> companies = companyRepository.findByBtCodeAndStatus(btCode, 1);
        List<CompanyResponse> companyResponses = companies.stream()
                .map(c -> {
                    List<TblShift> shifts = shiftRepository.findByCompanyCodeAndStatus(c.getCompanyCode(), 1);
                    return new CompanyResponse(c, shifts);
                })
                .collect(Collectors.toList());

        List<TblEmployee>    employees    = employeeRepository.findByBtCodeAndStatus(btCode, 1);

        Map<String, Object> data = new HashMap<>();
        data.put("departments",  departments);
        data.put("designations", designations);
        data.put("companies",    companyResponses);
        data.put("employees",    employees);

        return ApiResponse.success("Master data fetched successfully", data);
    }
}