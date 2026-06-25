package com.emptrack.api.service;

import com.emptrack.api.dto.*;
import com.emptrack.api.model.*;
import com.emptrack.api.repository.*;
import com.emptrack.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WageReportService {

    private final AdvanceRepository    advanceRepo;
    private final BonusRepository      bonusRepo;
    private final OvertimeRepository   overtimeRepo;
    private final EmployeeRepository   employeeRepo;
    private final DepartmentRepository departmentRepo;

    // ═══════════════════════════════════════════
    // 1. ADVANCE
    // ═══════════════════════════════════════════

    public ApiResponse<?> getAdvanceList(String btCode, String companyCode, String month) {
        YearMonth ym        = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate   = ym.atEndOfMonth();

        List<TblAdvance> advances = advanceRepo
                .findByBtCodeAndCompanyCodeAndRequestDateBetweenAndStatusOrderByRequestDateAsc(
                        btCode, companyCode, startDate, endDate, 1
                );

        if (advances.isEmpty()) {
            return ApiResponse.success("No advance entries for " + month,
                    AdvanceMonthlyResponse.builder()
                            .month(month)
                            .totalEntries(0)
                            .totalAmount(0)
                            .entries(Collections.emptyList())
                            .build()
            );
        }

        Map<String, TblEmployee> empMap  = fetchEmpMap(advances.stream().map(TblAdvance::getEmpCode).distinct().toList());
        Map<String, String>      deptMap = batchFetchDeptNames(empMap);

        List<AdvanceEntryResponse> entries = advances.stream()
                .map(a -> mapToAdvanceEntry(a, empMap, deptMap))
                .sorted(Comparator.comparing(AdvanceEntryResponse::getRequestDate))
                .toList();

        return ApiResponse.success("Advance report fetched successfully",
                AdvanceMonthlyResponse.builder()
                        .month(month)
                        .totalEntries(entries.size())
                        .totalAmount(entries.stream().mapToDouble(AdvanceEntryResponse::getAmount).sum())
                        .entries(entries)
                        .build()
        );
    }

    public ApiResponse<?> updateAdvance(String advanceId, AdvanceEntryRequest request) {
        TblAdvance advance = advanceRepo.findByAdvanceId(advanceId)
                .orElseThrow(() -> new RuntimeException("Advance not found: " + advanceId));

        if (!advance.getBtCode().equals(request.getBtCode()) ||
                !advance.getCompanyCode().equals(request.getCompanyCode())) {
            return ApiResponse.error(404,"Unauthorized: btCode/companyCode mismatch");
        }

        advance.setEmpCode(request.getEmpCode());
        advance.setRequestDate(request.getRequestDate());
        advance.setAmount(request.getAmount());
        advance.setRepayMonth(request.getRepayMonth());
        advance.setRemarks(request.getRemarks());
        advanceRepo.save(advance);

        return ApiResponse.success("Advance updated successfully",
                mapToAdvanceEntry(advance, Collections.emptyMap(), Collections.emptyMap())
        );
    }

    public ApiResponse<?> deleteAdvance(String advanceId, String btCode, String companyCode) {
        TblAdvance advance = advanceRepo.findByAdvanceId(advanceId)
                .orElseThrow(() -> new RuntimeException("Advance not found: " + advanceId));

        if (!advance.getBtCode().equals(btCode) ||
                !advance.getCompanyCode().equals(companyCode)) {
            return ApiResponse.error(404,"Unauthorized: btCode/companyCode mismatch");
        }

        advance.setStatus(0);
        advanceRepo.save(advance);

        return ApiResponse.success("Advance deleted successfully", null);
    }

    // ═══════════════════════════════════════════
    // 2. BONUS
    // ═══════════════════════════════════════════

    public ApiResponse<?> getBonusList(String btCode, String companyCode, String month) {
        YearMonth ym        = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate   = ym.atEndOfMonth();

        List<TblBonus> bonuses = bonusRepo
                .findByBtCodeAndCompanyCodeAndBonusDateBetweenAndStatusOrderByBonusDateAsc(
                        btCode, companyCode, startDate, endDate, 1
                );

        if (bonuses.isEmpty()) {
            return ApiResponse.success("No bonus entries for " + month,
                    BonusMonthlyResponse.builder()
                            .month(month)
                            .totalEntries(0)
                            .totalAmount(0)
                            .entries(Collections.emptyList())
                            .build()
            );
        }

        Map<String, TblEmployee> empMap  = fetchEmpMap(bonuses.stream().map(TblBonus::getEmpCode).distinct().toList());
        Map<String, String>      deptMap = batchFetchDeptNames(empMap);

        List<BonusEntryResponse> entries = bonuses.stream()
                .map(b -> mapToBonusEntry(b, empMap, deptMap))
                .sorted(Comparator.comparing(BonusEntryResponse::getBonusDate))
                .toList();

        return ApiResponse.success("Bonus report fetched successfully",
                BonusMonthlyResponse.builder()
                        .month(month)
                        .totalEntries(entries.size())
                        .totalAmount(entries.stream().mapToDouble(BonusEntryResponse::getAmount).sum())
                        .entries(entries)
                        .build()
        );
    }

    public ApiResponse<?> updateBonus(String bonusId, BonusEntryRequest request) {
        TblBonus bonus = bonusRepo.findByBonusId(bonusId)
                .orElseThrow(() -> new RuntimeException("Bonus not found: " + bonusId));

        if (!bonus.getBtCode().equals(request.getBtCode()) ||
                !bonus.getCompanyCode().equals(request.getCompanyCode())) {
            return ApiResponse.error(404,"Unauthorized: btCode/companyCode mismatch");
        }

        bonus.setEmpCode(request.getEmpCode());
        bonus.setBonusDate(request.getBonusDate());
        bonus.setBonusType(request.getBonusType());
        bonus.setAmount(request.getAmount());
        bonus.setRemarks(request.getRemarks());
        bonusRepo.save(bonus);

        return ApiResponse.success("Bonus updated successfully",
                mapToBonusEntry(bonus, Collections.emptyMap(), Collections.emptyMap())
        );
    }

    public ApiResponse<?> deleteBonus(String bonusId, String btCode, String companyCode) {
        TblBonus bonus = bonusRepo.findByBonusId(bonusId)
                .orElseThrow(() -> new RuntimeException("Bonus not found: " + bonusId));

        if (!bonus.getBtCode().equals(btCode) ||
                !bonus.getCompanyCode().equals(companyCode)) {
            return ApiResponse.error(404,"Unauthorized: btCode/companyCode mismatch");
        }

        bonus.setStatus(0);
        bonusRepo.save(bonus);

        return ApiResponse.success("Bonus deleted successfully", null);
    }

    // ═══════════════════════════════════════════
    // 3. OVERTIME
    // ═══════════════════════════════════════════

    public ApiResponse<?> getOvertimeList(String btCode, String companyCode, String month) {
        YearMonth ym        = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate   = ym.atEndOfMonth();

        List<TblOvertime> overtimes = overtimeRepo
                .findByBtCodeAndCompanyCodeAndOtDateBetweenAndStatusOrderByOtDateAsc(
                        btCode, companyCode, startDate, endDate, 1
                );

        if (overtimes.isEmpty()) {
            return ApiResponse.success("No overtime entries for " + month,
                    OvertimeMonthlyResponse.builder()
                            .month(month)
                            .totalEntries(0)
                            .totalHours(0)
                            .totalAmount(0)
                            .entries(Collections.emptyList())
                            .build()
            );
        }

        Map<String, TblEmployee> empMap  = fetchEmpMap(overtimes.stream().map(TblOvertime::getEmpCode).distinct().toList());
        Map<String, String>      deptMap = batchFetchDeptNames(empMap);

        List<OvertimeEntryResponse> entries = overtimes.stream()
                .map(o -> mapToOvertimeEntry(o, empMap, deptMap))
                .sorted(Comparator.comparing(OvertimeEntryResponse::getOtDate))
                .toList();

        return ApiResponse.success("Overtime report fetched successfully",
                OvertimeMonthlyResponse.builder()
                        .month(month)
                        .totalEntries(entries.size())
                        .totalHours(entries.stream().mapToDouble(OvertimeEntryResponse::getOtHours).sum())
                        .totalAmount(entries.stream().mapToDouble(OvertimeEntryResponse::getOtAmount).sum())
                        .entries(entries)
                        .build()
        );
    }

    public ApiResponse<?> updateOvertime(String otId, OvertimeEntryRequest request) {
        TblOvertime overtime = overtimeRepo.findByOtId(otId)
                .orElseThrow(() -> new RuntimeException("Overtime not found: " + otId));

        if (!overtime.getBtCode().equals(request.getBtCode()) ||
                !overtime.getCompanyCode().equals(request.getCompanyCode())) {
            return ApiResponse.error(404,"Unauthorized: btCode/companyCode mismatch");
        }

        overtime.setEmpCode(request.getEmpCode());
        overtime.setOtDate(request.getOtDate());
        overtime.setOtHours(request.getOtHours());
        overtime.setOtAmount(request.getOtAmount());
        overtime.setRemarks(request.getRemarks());
        overtimeRepo.save(overtime);

        return ApiResponse.success("Overtime updated successfully",
                mapToOvertimeEntry(overtime, Collections.emptyMap(), Collections.emptyMap())
        );
    }

    public ApiResponse<?> deleteOvertime(String otId, String btCode, String companyCode) {
        TblOvertime overtime = overtimeRepo.findByOtId(otId)
                .orElseThrow(() -> new RuntimeException("Overtime not found: " + otId));

        if (!overtime.getBtCode().equals(btCode) ||
                !overtime.getCompanyCode().equals(companyCode)) {
            return ApiResponse.error(404,"Unauthorized: btCode/companyCode mismatch");
        }

        overtime.setStatus(0);
        overtimeRepo.save(overtime);

        return ApiResponse.success("Overtime deleted successfully", null);
    }


    private AdvanceEntryResponse mapToAdvanceEntry(
            TblAdvance a,
            Map<String, TblEmployee> empMap,
            Map<String, String> deptMap
    ) {
        TblEmployee emp = empMap.get(a.getEmpCode());
        return AdvanceEntryResponse.builder()
                .id(a.getId())
                .advanceId(a.getAdvanceId())
                .btCode(a.getBtCode())
                .companyCode(a.getCompanyCode())
                .empCode(a.getEmpCode())
                .empName(emp != null ? emp.getName() : a.getEmpCode())
                .deptName(emp != null && emp.getDeptCode() != null
                        ? deptMap.getOrDefault(emp.getDeptCode(), "") : "")
                .requestDate(a.getRequestDate().toString())
                .amount(a.getAmount())
                .repayMonth(a.getRepayMonth())
                .remarks(a.getRemarks() != null ? a.getRemarks() : "")
                .status(a.getStatus())
                .build();
    }

    private BonusEntryResponse mapToBonusEntry(
            TblBonus b,
            Map<String, TblEmployee> empMap,
            Map<String, String> deptMap
    ) {
        TblEmployee emp = empMap.get(b.getEmpCode());
        return BonusEntryResponse.builder()
                .id(b.getId())
                .bonusId(b.getBonusId())
                .btCode(b.getBtCode())
                .companyCode(b.getCompanyCode())
                .empCode(b.getEmpCode())
                .empName(emp != null ? emp.getName() : b.getEmpCode())
                .deptName(emp != null && emp.getDeptCode() != null
                        ? deptMap.getOrDefault(emp.getDeptCode(), "") : "")
                .bonusDate(b.getBonusDate().toString())
                .bonusType(b.getBonusType())
                .amount(b.getAmount())
                .remarks(b.getRemarks() != null ? b.getRemarks() : "")
                .status(b.getStatus())
                .build();
    }

    private OvertimeEntryResponse mapToOvertimeEntry(
            TblOvertime o,
            Map<String, TblEmployee> empMap,
            Map<String, String> deptMap
    ) {
        TblEmployee emp = empMap.get(o.getEmpCode());
        return OvertimeEntryResponse.builder()
                .id(o.getId())
                .otId(o.getOtId())
                .btCode(o.getBtCode())
                .companyCode(o.getCompanyCode())
                .empCode(o.getEmpCode())
                .empName(emp != null ? emp.getName() : o.getEmpCode())
                .deptName(emp != null && emp.getDeptCode() != null
                        ? deptMap.getOrDefault(emp.getDeptCode(), "") : "")
                .otDate(o.getOtDate().toString())
                .otHours(o.getOtHours())
                .ratePerHour(o.getOtHours() != null && o.getOtHours() > 0
                        ? o.getOtAmount() / o.getOtHours() : 0.0)
                .otAmount(o.getOtAmount())
                .remarks(o.getRemarks() != null ? o.getRemarks() : "")
                .status(o.getStatus())
                .build();
    }

    // ═══════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════

    private Map<String, TblEmployee> fetchEmpMap(List<String> empCodes) {
        return employeeRepo.findByEmpCodeIn(empCodes)
                .stream()
                .collect(Collectors.toMap(TblEmployee::getEmpCode, e -> e));
    }

    private Map<String, String> batchFetchDeptNames(Map<String, TblEmployee> empMap) {
        List<String> deptCodes = empMap.values().stream()
                .map(TblEmployee::getDeptCode)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return departmentRepo.findByDeptCodeIn(deptCodes)
                .stream()
                .collect(Collectors.toMap(
                        TblDepartment::getDeptCode,
                        TblDepartment::getName
                ));
    }
}