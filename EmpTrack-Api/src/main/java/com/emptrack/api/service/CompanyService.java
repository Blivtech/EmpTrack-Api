package com.emptrack.api.service;

import com.emptrack.api.dto.CompanyRequest;
import com.emptrack.api.dto.CompanyResponse;
import com.emptrack.api.model.TblCompany;
import com.emptrack.api.model.TblShift;
import com.emptrack.api.repository.CompanyRepository;
import com.emptrack.api.repository.ShiftRepository;
import com.emptrack.api.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    // ── Code Generators ────────────────────────────────────
    private String generateCompanyCode() {
        return companyRepository.findTopByOrderByIdDesc()
                .map(c -> {
                    String last = c.getCompanyCode().replace("COM", "");
                    int next = Integer.parseInt(last) + 1;
                    return String.format("COM%03d", next);
                })
                .orElse("COM001");
    }

    private String generateShiftCode() {
        return shiftRepository.findTopByOrderByIdDesc()
                .map(s -> {
                    String last = s.getShiftCode().replace("SHF", "");
                    int next = Integer.parseInt(last) + 1;
                    return String.format("SHF%03d", next);
                })
                .orElse("SHF001");
    }

    // ── Save Company + Shifts ──────────────────────────────
    @Transactional
    public ApiResponse<?> saveCompany(CompanyRequest request) {

        // Validations
        if (request.getBtCode() == null || request.getBtCode().isEmpty())
            return ApiResponse.error(400, "bt_code is required");

        if (request.getName() == null || request.getName().isEmpty())
            return ApiResponse.error(400, "Company name is required");

        if (request.getShifts() == null || request.getShifts().isEmpty())
            return ApiResponse.error(400, "At least one shift is required");

        // Duplicate check
        if (companyRepository.existsByNameAndBtCode(request.getName(), request.getBtCode()))
            return ApiResponse.error(409, "Company name already exists");

        // Save company
        TblCompany company = new TblCompany();
        company.setBtCode(request.getBtCode());
        company.setCompanyCode(generateCompanyCode());
        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setCity(request.getCity());
        company.setState(request.getState());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setLogo(request.getLogo());
        company.setStatus(1);
        TblCompany savedCompany = companyRepository.save(company);

        // Save shifts
        List<TblShift> savedShifts = saveShifts(request.getBtCode(), savedCompany.getCompanyCode(), request.getShifts());

        return ApiResponse.success("Company saved successfully", new CompanyResponse(savedCompany, savedShifts));
    }

    // ── Update Company + Shifts ────────────────────────────
    @Transactional
    public ApiResponse<?> updateCompany(String code, CompanyRequest request) {

        // Validations
        if (request.getBtCode() == null || request.getBtCode().isEmpty())
            return ApiResponse.error(400, "bt_code is required");

        if (request.getName() == null || request.getName().isEmpty())
            return ApiResponse.error(400, "Company name is required");

        // Existence check
        TblCompany company = companyRepository.findByCompanyCode(code);
        if (company == null)
            return ApiResponse.error(404, "Company not found");

        // Duplicate check (exclude current id)
        if (companyRepository.existsByNameAndBtCodeAndCompanyCodeNot(request.getName(), request.getBtCode(), code))
            return ApiResponse.error(409, "Company name already exists");

        // Update company fields
        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setCity(request.getCity());
        company.setState(request.getState());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setLogo(request.getLogo());
        TblCompany updatedCompany = companyRepository.save(company);

        // Delete old shifts and save new ones
        List<TblShift> updatedShifts = new ArrayList<>();
        if (request.getShifts() != null && !request.getShifts().isEmpty()) {
            shiftRepository.deleteByCompanyCode(code);
            updatedShifts = saveShifts(request.getBtCode(), code, request.getShifts());
        } else {
            updatedShifts = shiftRepository.findByCompanyCodeAndStatus(code, 1);
        }

        return ApiResponse.success("Company updated successfully", new CompanyResponse(updatedCompany, updatedShifts));
    }

    // ── Delete Company ─────────────────────────────────────
    @Transactional
    public ApiResponse<?> deleteCompany(Long id) {

        TblCompany company = companyRepository.findById(id)
                .orElse(null);
        if (company == null)
            return ApiResponse.error(404, "Company not found");

        // Soft delete
        company.setStatus(0);
        companyRepository.save(company);

        return ApiResponse.success("Company deleted successfully", null);
    }

    // ── Get All Companies ──────────────────────────────────
    public ApiResponse<?> getAllCompanies(String btCode) {

        if (btCode == null || btCode.isEmpty())
            return ApiResponse.error(400, "bt_code is required");

        List<TblCompany> companies = companyRepository.findByBtCodeAndStatus(btCode, 1);

        List<CompanyResponse> response = companies.stream()
                .map(c -> {
                    List<TblShift> shifts = shiftRepository.findByCompanyCodeAndStatus(c.getCompanyCode(), 1);
                    return new CompanyResponse(c, shifts);
                })
                .collect(Collectors.toList());

        return ApiResponse.success("Companies fetched successfully", response);
    }

    // ── Helper: Save Shifts ────────────────────────────────
    private List<TblShift> saveShifts(String btCode, String companyCode, List<CompanyRequest.ShiftRequest> shiftRequests) {
        List<TblShift> savedShifts = new ArrayList<>();
        for (CompanyRequest.ShiftRequest shiftReq : shiftRequests) {
            if (shiftReq.getShiftName() == null || shiftReq.getShiftName().isEmpty()) continue;

            TblShift shift = new TblShift();
            shift.setBtCode(btCode);
            shift.setCompanyCode(companyCode);
            shift.setShiftCode(generateShiftCode());
            shift.setShiftName(shiftReq.getShiftName());
            shift.setStartTime(LocalTime.parse(shiftReq.getStartTime()));
            shift.setEndTime(LocalTime.parse(shiftReq.getEndTime()));
            shift.setStatus(1);
            savedShifts.add(shiftRepository.save(shift));
        }
        return savedShifts;
    }
}