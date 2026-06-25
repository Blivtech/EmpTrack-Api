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
public class MonthlyReportService {

    private final MonthlyReportRepository monthlyRepo;
    private final EmployeeRepository      employeeRepo;
    private final DepartmentRepository    departmentRepo;
    private final DesignationRepository   designationRepo;
    private final ShiftRepository         shiftRepo;

    // ─────────────────────────────────────
    // ✅ 1. Overall monthly report
    // ─────────────────────────────────────
    public ApiResponse<?> getMonthlyReport(
            String btCode,
            String companyCode,
            String month
    ) {
        YearMonth ym        = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate   = ym.atEndOfMonth();
        int workingDays     = getWorkingDays(startDate, endDate);

        // ✅ Fetch all details for month
        List<TblAttendanceDetail> allDetails = monthlyRepo
                .findByBtCodeAndCompanyCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        btCode, companyCode, startDate, endDate
                );

        if (allDetails.isEmpty()) {
            return ApiResponse.error(404,
                    "No attendance data for " + month
            );
        }

        // ✅ Batch fetch all needed data
        Map<String, TblEmployee> empMap   = fetchEmpMap(allDetails);
        Map<String, String>      deptMap  = batchFetchDeptNames(empMap);
        Map<String, String>      desgMap  = batchFetchDesgNames(empMap);
        Map<String, TblShift>    shiftMap = fetchShiftMap(allDetails);

        // ✅ Group by empCode
        Map<String, List<TblAttendanceDetail>> byEmp = allDetails.stream()
                .collect(Collectors.groupingBy(
                        TblAttendanceDetail::getEmpCode
                ));

        // ✅ Build summaries
        List<MonthlyEmployeeSummaryResponse> employees =
                buildEmployeeSummaries(
                        byEmp, empMap, deptMap, desgMap, shiftMap
                );

        // ✅ Totals
        int totalPresent = sumField(employees, MonthlyEmployeeSummaryResponse::getPresentDays);
        int totalAbsent  = sumField(employees, MonthlyEmployeeSummaryResponse::getAbsentDays);
        int totalHoliday = sumField(employees, MonthlyEmployeeSummaryResponse::getHolidayDays);
        int totalWeekOff = sumField(employees, MonthlyEmployeeSummaryResponse::getWeekOffDays);

        return ApiResponse.success(
                "Monthly report fetched successfully",
                MonthlyReportResponse.builder()
                        .btCode(btCode)
                        .companyCode(companyCode)
                        .month(month)
                        .totalEmployees(employees.size())
                        .totalPresent(totalPresent)
                        .totalAbsent(totalAbsent)
                        .totalHoliday(totalHoliday)
                        .totalWeekOff(totalWeekOff)
                        .workingDays(workingDays)
                        .employees(employees)
                        .build()
        );
    }

    // ─────────────────────────────────────
    // ✅ 2. Shift wise monthly report
    // ─────────────────────────────────────
    public ApiResponse<?> getMonthlyShiftReport(
            String btCode,
            String companyCode,
            String month,
            String shiftCode
    ) {
        YearMonth ym        = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate   = ym.atEndOfMonth();
        int workingDays     = getWorkingDays(startDate, endDate);

        // ✅ Fetch details for this shift only
        List<TblAttendanceDetail> allDetails = monthlyRepo
                .findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        btCode, companyCode, shiftCode, startDate, endDate
                );

        if (allDetails.isEmpty()) {
            return ApiResponse.error(404,
                    "No attendance data for shift " + shiftCode
            );
        }

        // ✅ Fetch shift info
        TblShift shift = shiftRepo
                .findByShiftCode(shiftCode);

        // ✅ Batch fetch
        Map<String, TblEmployee> empMap   = fetchEmpMap(allDetails);
        Map<String, String>      deptMap  = batchFetchDeptNames(empMap);
        Map<String, String>      desgMap  = batchFetchDesgNames(empMap);

        // ✅ ShiftMap — only one shift here
        Map<String, TblShift> shiftMap = new HashMap<>();
        if (shift != null) shiftMap.put(shift.getShiftCode(), shift);

        // ✅ Group + build
        Map<String, List<TblAttendanceDetail>> byEmp = allDetails.stream()
                .collect(Collectors.groupingBy(
                        TblAttendanceDetail::getEmpCode
                ));

        List<MonthlyEmployeeSummaryResponse> employees =
                buildEmployeeSummaries(
                        byEmp, empMap, deptMap, desgMap, shiftMap
                );

        int totalPresent = sumField(employees, MonthlyEmployeeSummaryResponse::getPresentDays);
        int totalAbsent  = sumField(employees, MonthlyEmployeeSummaryResponse::getAbsentDays);
        int totalHoliday = sumField(employees, MonthlyEmployeeSummaryResponse::getHolidayDays);
        int totalWeekOff = sumField(employees, MonthlyEmployeeSummaryResponse::getWeekOffDays);

        return ApiResponse.success(
                "Shift report fetched successfully",
                MonthlyShiftReportResponse.builder()
                        .shiftCode(shiftCode)
                        .shiftName(shift != null ? shift.getShiftName() : shiftCode)
                        .month(month)
                        .totalEmployees(employees.size())
                        .totalPresent(totalPresent)
                        .totalAbsent(totalAbsent)
                        .totalHoliday(totalHoliday)
                        .totalWeekOff(totalWeekOff)
                        .workingDays(workingDays)
                        .employees(employees)
                        .build()
        );
    }

    // ─────────────────────────────────────
    // ✅ 3. Employee monthly detail
    // ─────────────────────────────────────
    public ApiResponse<?> getMonthlyEmployeeDetail(
            String btCode,
            String companyCode,
            String month,
            String shiftCode,
            String empCode
    ) {
        YearMonth ym        = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate   = ym.atEndOfMonth();

        // ✅ Get employee details for full month
        List<TblAttendanceDetail> details = monthlyRepo
                .findByBtCodeAndCompanyCodeAndEmpCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        btCode, companyCode, empCode,
                        shiftCode, startDate, endDate
                );

        // ✅ Fetch employee info
        TblEmployee emp = employeeRepo
                .findByEmpCode(empCode);

        String deptName = "";
        String desgName = "";
        if (emp != null) {
            if (emp.getDeptCode() != null) {
                TblDepartment dept = departmentRepo.findByDeptCode(emp.getDeptCode());
                deptName = (dept != null) ? dept.getName() : "";
                // optional: log so you can spot data issues instead of silently swallowing them
                // if (dept == null) log.warn("No department found for deptCode={}", emp.getDeptCode());
            }
            if (emp.getDesgCode() != null) {
                TblDesignation desg = designationRepo.findByDesgCode(emp.getDesgCode());
                desgName = (desg != null) ? desg.getName() : "";
            }
        }

        // ✅ Fetch shift info
        TblShift shift = shiftRepo
                .findByShiftCode(shiftCode);

        // ✅ Map date → detail for quick lookup
        Map<LocalDate, TblAttendanceDetail> detailMap = details.stream()
                .collect(Collectors.toMap(
                        TblAttendanceDetail::getAttendanceDate,
                        d -> d,
                        (a, b) -> a
                ));

        // ✅ Categorize all dates in month
        List<String> presentDates = new ArrayList<>();
        List<String> absentDates  = new ArrayList<>();
        List<String> holidayDates = new ArrayList<>();
        List<String> weekOffDates = new ArrayList<>();

        int presentDays = 0, absentDays  = 0;
        int holidayDays = 0, weekOffDays = 0;

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateStr = current.toString();
            TblAttendanceDetail d = detailMap.get(current);

            if (d == null) {
                // ✅ No record = week off
                weekOffDates.add(dateStr);
                weekOffDays++;
            } else {
                String status = resolveStatus(d);
                switch (status) {
                    case "P" -> {
                        presentDates.add(dateStr);
                        presentDays++;
                    }
                    case "L" -> {
                        absentDates.add(dateStr);
                        absentDays++;
                    }
                    case "H" -> {
                        holidayDates.add(dateStr);
                        holidayDays++;
                    }
                    case "W" -> {
                        weekOffDates.add(dateStr);
                        weekOffDays++;
                    }
                }
            }
            current = current.plusDays(1);
        }

        int totalDays = presentDays + absentDays +
                holidayDays + weekOffDays;
        int pct = totalDays > 0
                ? (presentDays * 100 / totalDays) : 0;

        return ApiResponse.success(
                "Employee detail fetched successfully",
                MonthlyEmployeeDetailResponse.builder()
                        .empCode(empCode)
                        .empName(emp != null ? emp.getName() : empCode)
                        .department(emp != null ? emp.getDeptCode() : "")
                        .designation(emp != null ? emp.getDesgCode() : "")
                        .deptName(deptName)
                        .desgName(desgName)
                        .shiftCode(shiftCode)
                        .shiftName(shift != null ? shift.getShiftName() : shiftCode)
                        .month(month)
                        .presentDays(presentDays)
                        .absentDays(absentDays)
                        .holidayDays(holidayDays)
                        .weekOffDays(weekOffDays)
                        .totalDays(totalDays)
                        .attendancePercent(pct)
                        .presentDates(presentDates)
                        .absentDates(absentDates)
                        .holidayDates(holidayDates)
                        .weekOffDates(weekOffDates)
                        .build()
        );
    }

    // ─────────────────────────────────────
    // ✅ Build employee summaries
    // ─────────────────────────────────────
    private List<MonthlyEmployeeSummaryResponse> buildEmployeeSummaries(
            Map<String, List<TblAttendanceDetail>> byEmp,
            Map<String, TblEmployee>               empMap,
            Map<String, String>                    deptMap,
            Map<String, String>                    desgMap,
            Map<String, TblShift>                  shiftMap
    ) {
        return byEmp.entrySet().stream()
                .map(entry -> {
                    String empCode = entry.getKey();
                    List<TblAttendanceDetail> details = entry.getValue();
                    TblEmployee emp = empMap.get(empCode);

                    // ✅ Get shiftCode from detail rows
                    String shiftCode = details.stream()
                            .map(TblAttendanceDetail::getShiftCode)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse("");

                    // ✅ Count statuses
                    int presentDays = 0, absentDays  = 0;
                    int holidayDays = 0, weekOffDays = 0;

                    for (TblAttendanceDetail d : details) {
                        String status = resolveStatus(d);
                        switch (status) {
                            case "P", "L" -> presentDays++;
                            case "A"      -> absentDays++;
                            case "H"      -> holidayDays++;
                            case "W"      -> weekOffDays++;
                        }
                    }

                    int totalDays = presentDays + absentDays +
                            holidayDays + weekOffDays;
                    int pct = totalDays > 0
                            ? (presentDays * 100 / totalDays) : 0;

                    // ✅ Names
                    String deptName = emp != null && emp.getDeptCode() != null
                            ? deptMap.getOrDefault(emp.getDeptCode(), "") : "";
                    String desgName = emp != null && emp.getDesgCode() != null
                            ? desgMap.getOrDefault(emp.getDesgCode(), "") : "";

                    // ✅ Shift name from shiftMap
                    TblShift shift = shiftMap.get(shiftCode);

                    return MonthlyEmployeeSummaryResponse.builder()
                            .empCode(empCode)
                            .empName(emp != null ? emp.getName() : empCode)
                            .department(emp != null ? emp.getDeptCode() : "")
                            .designation(emp != null ? emp.getDesgCode() : "")
                            .deptName(deptName)
                            .desgName(desgName)
                            .shiftCode(shiftCode)
                            .shiftName(shift != null ? shift.getShiftName() : "")
                            .presentDays(presentDays)
                            .absentDays(absentDays)
                            .holidayDays(holidayDays)
                            .weekOffDays(weekOffDays)
                            .totalDays(totalDays)
                            .attendancePercent(pct)
                            .build();
                })
                .sorted(Comparator.comparing(
                        MonthlyEmployeeSummaryResponse::getEmpName
                ))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────
    // ✅ Fetch empMap from detail list
    // ─────────────────────────────────────
    private Map<String, TblEmployee> fetchEmpMap(
            List<TblAttendanceDetail> details
    ) {
        List<String> empCodes = details.stream()
                .map(TblAttendanceDetail::getEmpCode)
                .distinct()
                .collect(Collectors.toList());

        return employeeRepo.findByEmpCodeIn(empCodes)
                .stream()
                .collect(Collectors.toMap(
                        TblEmployee::getEmpCode, e -> e
                ));
    }

    // ─────────────────────────────────────
    // ✅ Fetch shiftMap from detail list
    // ─────────────────────────────────────
    private Map<String, TblShift> fetchShiftMap(
            List<TblAttendanceDetail> details
    ) {
        List<String> shiftCodes = details.stream()
                .map(TblAttendanceDetail::getShiftCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        return shiftRepo.findByShiftCodeIn(shiftCodes)
                .stream()
                .collect(Collectors.toMap(
                        TblShift::getShiftCode, s -> s
                ));
    }

    // ─────────────────────────────────────
    // ✅ Batch fetch dept names
    // ─────────────────────────────────────
    private Map<String, String> batchFetchDeptNames(
            Map<String, TblEmployee> empMap
    ) {
        List<String> deptCodes = empMap.values().stream()
                .map(TblEmployee::getDeptCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        return departmentRepo.findByDeptCodeIn(deptCodes)
                .stream()
                .collect(Collectors.toMap(
                        TblDepartment::getDeptCode,
                        TblDepartment::getName
                ));
    }

    // ─────────────────────────────────────
    // ✅ Batch fetch desg names
    // ─────────────────────────────────────
    private Map<String, String> batchFetchDesgNames(
            Map<String, TblEmployee> empMap
    ) {
        List<String> desgCodes = empMap.values().stream()
                .map(TblEmployee::getDesgCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        return designationRepo.findByDesgCodeIn(desgCodes)
                .stream()
                .collect(Collectors.toMap(
                        TblDesignation::getDesgCode,
                        TblDesignation::getName
                ));
    }

    // ─────────────────────────────────────
    // ✅ Resolve status
    // ─────────────────────────────────────
    private String resolveStatus(TblAttendanceDetail d) {
        if (d.getDayPlanStatus() == null) return "A";
        return switch (d.getDayPlanStatus()) {
            case 2  -> "W";
            case 3  -> "H";
            case 4  -> "A";
            default -> {
                if (d.getPresentCount() != null) {
                    if (d.getPresentCount() >= 1.0) yield "P";
                    if (d.getPresentCount() > 0.0)  yield "L";
                }
                yield "A";
            }
        };
    }

    // ─────────────────────────────────────
    // ✅ Get working days Mon–Sat
    // ─────────────────────────────────────
    private int getWorkingDays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate current = start;
        while (!current.isAfter(end)) {
            if (current.getDayOfWeek().getValue() != 7) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }

    // ─────────────────────────────────────
    // ✅ Sum helper
    // ─────────────────────────────────────
    private int sumField(
            List<MonthlyEmployeeSummaryResponse> list,
            java.util.function.ToIntFunction<MonthlyEmployeeSummaryResponse> fn
    ) {
        return list.stream().mapToInt(fn).sum();
    }
}