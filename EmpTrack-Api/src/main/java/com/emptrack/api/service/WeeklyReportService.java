package com.emptrack.api.service;

import com.emptrack.api.dto.*;
import com.emptrack.api.model.*;
import com.emptrack.api.repository.*;
import com.emptrack.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportRepository weeklyRepo;
    private final EmployeeRepository     employeeRepo;
    private final DepartmentRepository   departmentRepo;
    private final DesignationRepository  designationRepo;
    private final ShiftRepository        shiftRepo;

    // ─────────────────────────────────────
    // ✅ 1. Weekly summary — all shifts
    // ─────────────────────────────────────
    public ApiResponse<?> getWeeklyReport(
        String btCode,
        String companyCode,
        String weekStartStr,
        String weekEndStr
    ) {
        LocalDate weekStart = LocalDate.parse(weekStartStr);
        LocalDate weekEnd   = LocalDate.parse(weekEndStr);
        int workDays        = getWorkDays(weekStart, weekEnd);

        // ✅ Fetch all details for week
        List<TblAttendanceDetail> allDetails = weeklyRepo
            .findByBtCodeAndCompanyCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                btCode, companyCode, weekStart, weekEnd
            );

        if (allDetails.isEmpty()) {
            return ApiResponse.error(404,
                "No attendance data for this week"
            );
        }

        // ✅ Batch fetch
        Map<String, TblEmployee> empMap   = fetchEmpMap(allDetails);
        Map<String, String>      deptMap  = batchFetchDeptNames(empMap);
        Map<String, String>      desgMap  = batchFetchDesgNames(empMap);
        Map<String, TblShift>    shiftMap = fetchShiftMap(allDetails);

        // ✅ Group by shiftCode
        Map<String, List<TblAttendanceDetail>> byShift = allDetails.stream()
            .collect(Collectors.groupingBy(
                TblAttendanceDetail::getShiftCode
            ));

        // ✅ Build shift summaries
        List<WeeklyShiftSummaryResponse> shifts = byShift.entrySet()
            .stream()
            .map(entry -> {
                String shiftCode = entry.getKey();
                List<TblAttendanceDetail> shiftDetails = entry.getValue();
                TblShift shift = shiftMap.get(shiftCode);

                // ✅ Group by empCode within shift
                Map<String, List<TblAttendanceDetail>> byEmp =
                    shiftDetails.stream()
                        .collect(Collectors.groupingBy(
                            TblAttendanceDetail::getEmpCode
                        ));

                // ✅ Build employee summaries for this shift
                List<WeeklyEmployeeSummaryResponse> employees =
                    buildEmployeeSummaries(
                        byEmp, empMap, deptMap,
                        desgMap, shiftMap
                    );

                // ✅ Submitted days = unique dates
                int submittedDays = (int) shiftDetails.stream()
                    .map(TblAttendanceDetail::getAttendanceDate)
                    .distinct()
                    .count();

                int totalPresent = sumField(employees, WeeklyEmployeeSummaryResponse::getPresentDays);
                int totalAbsent  = sumField(employees, WeeklyEmployeeSummaryResponse::getAbsentDays);
                int totalHoliday = sumField(employees, WeeklyEmployeeSummaryResponse::getHolidayDays);
                int totalWeekOff = sumField(employees, WeeklyEmployeeSummaryResponse::getWeekOffDays);

                return WeeklyShiftSummaryResponse.builder()
                    .shiftCode(shiftCode)
                    .shiftName(shift != null ? shift.getShiftName() : shiftCode)
                    .startTime(shift != null
                        ? shift.getStartTime().toString().substring(0, 5) : "")
                    .endTime(shift != null
                        ? shift.getEndTime().toString().substring(0, 5) : "")
                    .weekStart(weekStartStr)
                    .weekEnd(weekEndStr)
                    .totalEmployees(employees.size())
                    .totalPresent(totalPresent)
                    .totalAbsent(totalAbsent)
                    .totalHoliday(totalHoliday)
                    .totalWeekOff(totalWeekOff)
                    .submittedDays(submittedDays)
                    .totalDays(workDays)
                    .employees(employees)
                    .build();
            })
            .sorted(Comparator.comparing(
                WeeklyShiftSummaryResponse::getShiftName
            ))
            .collect(Collectors.toList());

        // ✅ Overall totals
        int totalPresent = shifts.stream().mapToInt(WeeklyShiftSummaryResponse::getTotalPresent).sum();
        int totalAbsent  = shifts.stream().mapToInt(WeeklyShiftSummaryResponse::getTotalAbsent).sum();
        int totalHoliday = shifts.stream().mapToInt(WeeklyShiftSummaryResponse::getTotalHoliday).sum();
        int totalWeekOff = shifts.stream().mapToInt(WeeklyShiftSummaryResponse::getTotalWeekOff).sum();

        return ApiResponse.success(
            "Weekly report fetched successfully",
            WeeklyReportResponse.builder()
                .btCode(btCode)
                .companyCode(companyCode)
                .weekStart(weekStartStr)
                .weekEnd(weekEndStr)
                .totalEmployees(
                    shifts.stream()
                        .mapToInt(WeeklyShiftSummaryResponse::getTotalEmployees)
                        .sum()
                )
                .totalPresent(totalPresent)
                .totalAbsent(totalAbsent)
                .totalHoliday(totalHoliday)
                .totalWeekOff(totalWeekOff)
                .workDays(workDays)
                .shifts(shifts)
                .build()
        );
    }

    // ─────────────────────────────────────
    // ✅ 2. Shift employee list
    // ─────────────────────────────────────
    public ApiResponse<?> getShiftEmployees(
        String btCode,
        String companyCode,
        String weekStartStr,
        String weekEndStr,
        String shiftCode,
        String type             // PRESENT / ABSENT / HOLIDAY_WO
    ) {
        LocalDate weekStart = LocalDate.parse(weekStartStr);
        LocalDate weekEnd   = LocalDate.parse(weekEndStr);

        // ✅ Fetch shift details
        List<TblAttendanceDetail> allDetails = weeklyRepo
            .findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                btCode, companyCode, shiftCode, weekStart, weekEnd
            );

        if (allDetails.isEmpty()) {
            return ApiResponse.error(404,
                "No data for this shift"
            );
        }

        // ✅ Batch fetch
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
        List<WeeklyEmployeeSummaryResponse> all =
            buildEmployeeSummaries(
                byEmp, empMap, deptMap, desgMap, shiftMap
            );

        // ✅ Filter by type
        List<WeeklyEmployeeSummaryResponse> filtered = switch (type) {
            case "PRESENT"    -> all.stream()
                .filter(e -> e.getPresentDays() > 0)
                .collect(Collectors.toList());
            case "ABSENT"     -> all.stream()
                .filter(e -> e.getAbsentDays() > 0)
                .collect(Collectors.toList());
            case "HOLIDAY_WO" -> all.stream()
                .filter(e -> e.getHolidayDays() + e.getWeekOffDays() > 0)
                .collect(Collectors.toList());
            default           -> all;
        };

        return ApiResponse.success(
            "Shift employees fetched successfully",
            filtered
        );
    }

    // ─────────────────────────────────────
    // ✅ 3. Employee weekly detail
    // ─────────────────────────────────────
    public ApiResponse<?> getEmployeeWeeklyDetail(
        String btCode,
        String companyCode,
        String weekStartStr,
        String weekEndStr,
        String shiftCode,
        String empCode
    ) {
        LocalDate weekStart = LocalDate.parse(weekStartStr);
        LocalDate weekEnd   = LocalDate.parse(weekEndStr);

        // ✅ All days in week
        List<LocalDate> allDays = getAllDays(weekStart, weekEnd);

        // ✅ Fetch employee details for week
        List<TblAttendanceDetail> details = weeklyRepo
            .findByBtCodeAndCompanyCodeAndEmpCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                btCode, companyCode, empCode,
                shiftCode, weekStart, weekEnd
            );

        // ✅ Fetch employee + shift + dept + desg
        TblEmployee emp = employeeRepo
            .findByEmpCode(empCode);

        TblShift shift = shiftRepo
            .findByShiftCode(shiftCode);

        String deptName = "";
        String desgName = "";
        if (emp != null) {
            if (emp.getDeptCode() != null) {
                deptName = departmentRepo
                    .findByDeptCode(emp.getDeptCode()).getName();
            }
            if (emp.getDesgCode() != null) {
                desgName = designationRepo
                    .findByDesgCode(emp.getDesgCode()).getName();


            }
        }

        // ✅ Map date → detail
        Map<LocalDate, TblAttendanceDetail> detailMap = details.stream()
            .collect(Collectors.toMap(
                TblAttendanceDetail::getAttendanceDate,
                d -> d,
                (a, b) -> a
            ));

        // ✅ Build day-by-day status list
        List<DailyStatusResponse> dailyStatus = new ArrayList<>();
        int presentDays = 0, absentDays  = 0;
        int holidayDays = 0, weekOffDays = 0;
        int lateDays    = 0;

        for (LocalDate day : allDays) {
            TblAttendanceDetail d = detailMap.get(day);
            String status;
            String statusLabel;

            if (d == null) {
                // ✅ No record = week off
                status      = "W";
                statusLabel = "Week Off";
                weekOffDays++;
            } else {
                status      = resolveStatus(d);
                statusLabel = statusLabel(status);
                switch (status) {
                    case "P"  -> presentDays++;
                    case "L"  -> { presentDays++; lateDays++; }
                    case "A"  -> absentDays++;
                    case "H"  -> holidayDays++;
                    case "W"  -> weekOffDays++;
                }
            }

            dailyStatus.add(DailyStatusResponse.builder()
                .date(day.toString())
                .dayName(day.getDayOfWeek()
                    .getDisplayName(
                        TextStyle.SHORT,
                        Locale.ENGLISH
                    )
                )
                .status(status)
                .statusLabel(statusLabel)
                .build()
            );
        }

        int totalDays = presentDays + absentDays +
                        holidayDays + weekOffDays;
        int pct = totalDays > 0
            ? (presentDays * 100 / totalDays) : 0;

        return ApiResponse.success(
            "Employee weekly detail fetched",
            WeeklyEmployeeDetailResponse.builder()
                .empCode(empCode)
                .empName(emp != null ? emp.getName() : empCode)
                .department(emp != null ? emp.getDeptCode() : "")
                .designation(emp != null ? emp.getDesgCode() : "")
                .deptName(deptName)
                .desgName(desgName)
                .shiftCode(shiftCode)
                .shiftName(shift != null
                    ? shift.getShiftName() : shiftCode)
                .weekStart(weekStartStr)
                .weekEnd(weekEndStr)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .holidayDays(holidayDays)
                .weekOffDays(weekOffDays)
                .lateDays(lateDays)
                .totalDays(totalDays)
                .attendancePercent(pct)
                .dailyStatus(dailyStatus)
                .build()
        );
    }

    // ─────────────────────────────────────
    // ✅ Build employee summaries
    // ─────────────────────────────────────
    private List<WeeklyEmployeeSummaryResponse> buildEmployeeSummaries(
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

                // ✅ ShiftCode from detail
                String shiftCode = details.stream()
                    .map(TblAttendanceDetail::getShiftCode)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("");

                int presentDays = 0, absentDays  = 0;
                int holidayDays = 0, weekOffDays = 0;
                int lateDays    = 0;

                for (TblAttendanceDetail d : details) {
                    String status = resolveStatus(d);
                    switch (status) {
                        case "P"  -> presentDays++;
                        case "L"  -> { presentDays++; lateDays++; }
                        case "A"  -> absentDays++;
                        case "H"  -> holidayDays++;
                        case "W"  -> weekOffDays++;
                    }
                }

                int totalDays = presentDays + absentDays +
                                holidayDays + weekOffDays;
                int pct = totalDays > 0
                    ? (presentDays * 100 / totalDays) : 0;

                String deptName = emp != null && emp.getDeptCode() != null
                    ? deptMap.getOrDefault(emp.getDeptCode(), "") : "";
                String desgName = emp != null && emp.getDesgCode() != null
                    ? desgMap.getOrDefault(emp.getDesgCode(), "") : "";

                TblShift shift = shiftMap.get(shiftCode);

                return WeeklyEmployeeSummaryResponse.builder()
                    .empCode(empCode)
                    .empName(emp != null ? emp.getName() : empCode)
                    .department(emp != null ? emp.getDeptCode() : "")
                    .designation(emp != null ? emp.getDesgCode() : "")
                    .deptName(deptName)
                    .desgName(desgName)
                    .shiftCode(shiftCode)
                    .shiftName(shift != null
                        ? shift.getShiftName() : "")
                    .presentDays(presentDays)
                    .absentDays(absentDays)
                    .holidayDays(holidayDays)
                    .weekOffDays(weekOffDays)
                    .lateDays(lateDays)
                    .totalDays(totalDays)
                    .attendancePercent(pct)
                    .build();
            })
            .sorted(Comparator.comparing(
                WeeklyEmployeeSummaryResponse::getEmpName
            ))
            .collect(Collectors.toList());
    }

    // ─────────────────────────────────────
    // ✅ Fetch empMap
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
    // ✅ Fetch shiftMap
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
    // ✅ Status label
    // ─────────────────────────────────────
    private String statusLabel(String status) {
        return switch (status) {
            case "P"  -> "Present";
            case "L"  -> "Late";
            case "A"  -> "Absent";
            case "H"  -> "Holiday";
            case "W"  -> "Week Off";
            default   -> status;
        };
    }

    // ─────────────────────────────────────
    // ✅ Get work days Mon–Sat
    // ─────────────────────────────────────
    private int getWorkDays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate current = start;
        while (!current.isAfter(end)) {
            if (current.getDayOfWeek().getValue() != 7) count++;
            current = current.plusDays(1);
        }
        return count;
    }

    private List<LocalDate> getAllDays(
        LocalDate start, LocalDate end
    ) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }



    private int sumField(
            List<WeeklyEmployeeSummaryResponse> list,
            ToIntFunction<WeeklyEmployeeSummaryResponse> fn
    ) {
        return list.stream().mapToInt(fn).sum();
    }

    // ─────────────────────────────────────
// ✅ Weekly overall — all shifts combined
// ─────────────────────────────────────
    public ApiResponse<?> getWeeklyOverallReport(
            String btCode,
            String companyCode,
            String weekStartStr,
            String weekEndStr
    ) {
        LocalDate weekStart = LocalDate.parse(weekStartStr);
        LocalDate weekEnd   = LocalDate.parse(weekEndStr);
        int workDays        = getWorkDays(weekStart, weekEnd);

        // ✅ Fetch all details for week
        List<TblAttendanceDetail> allDetails = weeklyRepo
                .findByBtCodeAndCompanyCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        btCode, companyCode, weekStart, weekEnd
                );

        if (allDetails.isEmpty()) {
            return ApiResponse.error(404,
                    "No attendance data for this week"
            );
        }

        // ✅ Batch fetch
        Map<String, TblEmployee> empMap   = fetchEmpMap(allDetails);
        Map<String, String>      deptMap  = batchFetchDeptNames(empMap);
        Map<String, String>      desgMap  = batchFetchDesgNames(empMap);
        Map<String, TblShift>    shiftMap = fetchShiftMap(allDetails);

        // ✅ Group by empCode
        Map<String, List<TblAttendanceDetail>> byEmp = allDetails.stream()
                .collect(Collectors.groupingBy(
                        TblAttendanceDetail::getEmpCode
                ));

        // ✅ Build employee summaries
        List<WeeklyEmployeeSummaryResponse> employees =
                buildEmployeeSummaries(
                        byEmp, empMap, deptMap, desgMap, shiftMap
                );

        // ✅ Totals
        int totalPresent = sumField(employees, WeeklyEmployeeSummaryResponse::getPresentDays);
        int totalAbsent  = sumField(employees, WeeklyEmployeeSummaryResponse::getAbsentDays);
        int totalHoliday = sumField(employees, WeeklyEmployeeSummaryResponse::getHolidayDays);
        int totalWeekOff = sumField(employees, WeeklyEmployeeSummaryResponse::getWeekOffDays);

        return ApiResponse.success(
                "Weekly overall report fetched",
                WeeklyOverallReportResponse.builder()
                        .btCode(btCode)
                        .companyCode(companyCode)
                        .weekStart(weekStartStr)
                        .weekEnd(weekEndStr)
                        .totalEmployees(employees.size())
                        .totalPresent(totalPresent)
                        .totalAbsent(totalAbsent)
                        .totalHoliday(totalHoliday)
                        .totalWeekOff(totalWeekOff)
                        .workDays(workDays)
                        .employees(employees)
                        .build()
        );
    }

    // ─────────────────────────────────────
// ✅ Weekly shift wise
// ─────────────────────────────────────
    public ApiResponse<?> getWeeklyShiftReport(
            String btCode,
            String companyCode,
            String weekStartStr,
            String weekEndStr,
            String shiftCode
    ) {
        LocalDate weekStart = LocalDate.parse(weekStartStr);
        LocalDate weekEnd   = LocalDate.parse(weekEndStr);
        int workDays        = getWorkDays(weekStart, weekEnd);

        // ✅ Fetch details for this shift only
        List<TblAttendanceDetail> allDetails = weeklyRepo
                .findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        btCode, companyCode, shiftCode, weekStart, weekEnd
                );

        if (allDetails.isEmpty()) {
            return ApiResponse.error(404,
                    "No data for shift " + shiftCode
            );
        }

        // ✅ Fetch shift info
        TblShift shift = shiftRepo
                .findByShiftCode(shiftCode);

        // ✅ Batch fetch
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
        List<WeeklyEmployeeSummaryResponse> employees =
                buildEmployeeSummaries(
                        byEmp, empMap, deptMap, desgMap, shiftMap
                );

        int totalPresent = sumField(employees, WeeklyEmployeeSummaryResponse::getPresentDays);
        int totalAbsent  = sumField(employees, WeeklyEmployeeSummaryResponse::getAbsentDays);
        int totalHoliday = sumField(employees, WeeklyEmployeeSummaryResponse::getHolidayDays);
        int totalWeekOff = sumField(employees, WeeklyEmployeeSummaryResponse::getWeekOffDays);

        return ApiResponse.success(
                "Weekly shift report fetched",
                WeeklyShiftReportResponse.builder()
                        .shiftCode(shiftCode)
                        .shiftName(shift != null ? shift.getShiftName() : shiftCode)
                        .weekStart(weekStartStr)
                        .weekEnd(weekEndStr)
                        .totalEmployees(employees.size())
                        .totalPresent(totalPresent)
                        .totalAbsent(totalAbsent)
                        .totalHoliday(totalHoliday)
                        .totalWeekOff(totalWeekOff)
                        .workDays(workDays)
                        .employees(employees)
                        .build()
        );
    }








}