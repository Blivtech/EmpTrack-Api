package com.emptrack.api.service;

import com.emptrack.api.dto.CompanyResponse;
import com.emptrack.api.dto.ProductResponse;
import com.emptrack.api.dto.WorkTypeResponse;
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

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WorkTypeRepository workTypeRepository;

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

        List<TblProduct> products = productRepository
                .findByBtCodeAndStatus(btCode, 1);

        List<ProductResponse> productResponses = products.stream()
                .map(p -> {
                    List<TblWorkType> workTypes = workTypeRepository
                            .findByProductIdAndStatus(p.getProductId(), 1);

                    // ✅ Use builder — matches your @Builder annotation
                    return ProductResponse.builder()
                            .productId(p.getProductId())
                            .btCode(p.getBtCode())
                            .companyCode(p.getCompanyCode())
                            .productName(p.getProductName())
                            .description(p.getDescription())
                            .status(p.getStatus())
                            .createdAt(p.getCreatedAt().toString())
                            .workTypes(workTypes.stream()
                                    .map(wt -> WorkTypeResponse.builder()
                                            .workTypeId(wt.getWorkTypeId())
                                            .productId(wt.getProductId())
                                            .workTypeName(wt.getWorkTypeName())
                                            .ratePerPiece(wt.getRatePerPiece())
                                            .unit(wt.getUnit())
                                            .colorTag(wt.getColorTag())
                                            .status(wt.getStatus())
                                            .build()
                                    )
                                    .collect(Collectors.toList())
                            )
                            .build();
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("departments",  departments);
        data.put("designations", designations);
        data.put("companies",    companyResponses);
        data.put("employees",    employees);
        data.put("products", productResponses);

        return ApiResponse.success("Master data fetched successfully", data);
    }
}