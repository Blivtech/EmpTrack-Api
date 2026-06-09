package com.emptrack.api.service;

import com.emptrack.api.model.TblDesignation;
import com.emptrack.api.repository.DesignationRepository;
import com.emptrack.api.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    private String generateDesgCode() {
        return designationRepository.findTopByOrderByIdDesc()
                .map(d -> {
                    String last = d.getDesgCode().replace("DES", "");
                    int next = Integer.parseInt(last) + 1;
                    return String.format("DES%03d", next);
                })
                .orElse("DES001");
    }

    public ApiResponse<?> saveDesignation(TblDesignation request) {

        if (request.getBtCode() == null || request.getBtCode().isEmpty()) {
            return ApiResponse.error(400, "bt_code is required");
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            return ApiResponse.error(400, "Designation name is required");
        }

        if (designationRepository.existsByNameAndBtCode(request.getName(), request.getBtCode())) {
            return ApiResponse.error(409, "Designation name already exists");
        }

        request.setDesgCode(generateDesgCode());
        request.setStatus(1);
        TblDesignation saved = designationRepository.save(request);

        return ApiResponse.success("Designation saved successfully", saved);
    }
}