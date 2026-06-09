package com.emptrack.api.service;

import com.emptrack.api.response.ApiResponse;
import com.emptrack.api.model.TblDepartment;
import com.emptrack.api.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    private String generateDeptCode() {
        return departmentRepository.findTopByOrderByIdDesc()
                .map(d -> {
                    String last = d.getDeptCode().replace("DEP", "");
                    int next = Integer.parseInt(last) + 1;
                    return String.format("DEP%03d", next);
                })
                .orElse("DEP001");
    }

    public ApiResponse<?> saveDepartment(TblDepartment request) {

        if (request.getBtCode() == null || request.getBtCode().isEmpty()) {
            return ApiResponse.error(400, "bt_code is required");
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            return ApiResponse.error(400, "Department name is required");
        }

        if (departmentRepository.existsByNameAndBtCode(request.getName(), request.getBtCode())) {
            return ApiResponse.error(409, "Department name already exists");
        }

        request.setDeptCode(generateDeptCode());
        request.setStatus(1);
        TblDepartment saved = departmentRepository.save(request);

        return ApiResponse.success("Department saved successfully", saved);
    }
}