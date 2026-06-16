package com.emptrack.api.service;

import com.emptrack.api.dto.*;
import com.emptrack.api.model.*;
import com.emptrack.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportRepository  attendanceRepo;
    private final WeeklyDetailRepository  detailRepo;
    private final ShiftRepository         shiftRepo;
    private final EmployeeRepository      employeeRepo;

    // ─────────────────────────────────────
    // ✅ 1. Weekly summary — all shifts
    // ─────────────────────────────────────
    public WeeklyReportResponse getWeeklySummary(
        String btCode,
        String companyCode,
        String weekStartStr,
        String weekEndStr
    ) {
        LocalDate weekStart = LocalDate.parse(weekStartStr);
        LocalDate weekEnd   = LocalDate.parse(weekEndStr);

        // ✅ Get working days in range (Mon–Sat, skip Sun)
        List<LocalDate> workDays = getWorkDays(weekStart, weekEnd);

        // ✅ Get all shifts for company
        List<TblShift> shifts = shiftRepo
            .findByCompanyCodeAndStatus(companyCode, 1);

        // ✅ Build per-shift summary
        List<WeeklyShiftSummaryResponse> shiftSummaries = shifts.stream()
            .map(shift -> buildShiftWeeklySummary(
                btCode, companyCode, shift, weekStart, weekEnd, workDays
            ))
            .collect(Collectors.toList());

        // ✅ Overall totals
        int totalPresent = shiftSummaries.stream()
            .mapToInt(WeeklyShiftSummaryResponse::getTotalPresent).sum();
        int totalAbsent  = shiftSummaries.stream()
            .mapToInt(WeeklyShiftSummaryResponse::getTotalAbsent).sum();

        return WeeklyReportResponse.builder()
            .btCode(btCode)
            .companyCode(companyCode)
            .weekStart(weekStartStr)
            .weekEnd(weekEndStr)
            .totalPresent(totalPresent)
            .totalAbsent(totalAbsent)
            .workDays(workDays.size())
            .shifts(shiftSummaries)
            .build();
    }

    // ─────────────────────────────────────
    // ✅ 2. Shift employee list — Present or Absent
    // ─────────────────────────────────────
    public List<WeeklyShiftEmployeeResponse> getShiftEmployees(
        String btCode,
        String companyCode,
        String weekStartStr,
        String weekEndStr,
        String shiftCode,
        String type         // PRESENT or ABSENT
    ) {
        LocalDate weekStart = LocalDate.parse(weekStartStr);
        LocalDate weekEnd   = LocalDate.parse(weekEndStr);

        // ✅ Get all detail rows for this shift + week
        List<TblAttendanceDetail> allDetails = detailRepo
            .findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetween(
                btCode, companyCode, shiftCode, weekStart, weekEnd
            );

        // ✅ Group by empCode
        Map<String, List<TblAttendanceDetail>> byEmp = allDetails.stream()
            .collect(Collectors.groupingBy(TblAttendanceDetail::getEmpCode));

        // ✅ Batch fetch employees
        List<String> empCodes = new ArrayList<>(byEmp.keySet());
        Map<String, TblEmployee> empMap = employeeRepo
            .findByEmpCodeIn(empCodes).stream()
            .collect(Collectors.toMap(TblEmployee::getEmpCode, e -> e));

        // ✅ Get total working days
        int totalDays = getWorkDays(weekStart, weekEnd).size();

        // ✅ Build employee summaries + filter by type
        List<WeeklyShiftEmployeeResponse> result = new ArrayList<>();

        byEmp.forEach((empCode, details) -> {
            int presentDays = (int) details.stream()
                .filter(d -> d.getPresentCount() != null
                          && d.getPresentCount() > 0)
                .count();
            int absentDays  = totalDays - presentDays;

            // ✅ Filter
            boolean include = type.equals("PRESENT")
                ? presentDays > 0
                : absentDays  > 0;

            if (!include) return;

            TblEmployee emp = empMap.get(empCode);
            int pct = totalDays > 0
                ? (presentDays * 100 / totalDays) : 0;

            result.add(WeeklyShiftEmployeeResponse.builder()
                .empCode(empCode)
                .empName(emp != null ? emp.getName() : empCode)
                .department(emp != null ? emp.getDeptCode() : "")
                .designation(emp != null ? emp.getDesgCode() : "")
                .presentDays(presentDays)
                .absentDays(absentDays)
                .totalDays(totalDays)
                .attendancePercent(pct)
                .build()
            );
        });

        // ✅ Sort by empName
        result.sort(Comparator.comparing(WeeklyShiftEmployeeResponse::getEmpName));
        return result;
    }

    // ─────────────────────────────────────
    // ✅ 3. Employee weekly detail — day by day
    // ─────────────────────────────────────
    public EmployeeWeeklyDetailResponse getEmployeeDetail(
        String btCode,
        String companyCode,
        String weekStartStr,
        String weekEndStr,
        String shiftCode,
        String empCode
    ) {
        LocalDate weekStart = LocalDate.parse(weekStartStr);
        LocalDate weekEnd   = LocalDate.parse(weekEndStr);

        // ✅ All days in week (Mon–Sun)
        List<LocalDate> allDays = getAllDays(weekStart, weekEnd);

        // ✅ Get attendance details for this emp + shift + week
        List<TblAttendanceDetail> details = detailRepo
            .findByBtCodeAndCompanyCodeAndEmpCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                btCode, companyCode, empCode, shiftCode, weekStart, weekEnd
            );

        // ✅ Map date → detail for quick lookup
        Map<LocalDate, TblAttendanceDetail> detailMap = details.stream()
            .collect(Collectors.toMap(
                TblAttendanceDetail::getAttendanceDate,
                d -> d,
                (a, b) -> a
            ));

        // ✅ Fetch employee info
        TblEmployee emp = employeeRepo.findByEmpCode(empCode);

        // ✅ Get shift info
        TblShift shift = shiftRepo.findByShiftCode(shiftCode);

        // ✅ Build day-by-day status list
        int presentDays = 0, absentDays = 0;
        int lateDays = 0, holidayDays = 0, weekOffDays = 0;

        List<DailyStatusResponse> dailyStatus = new ArrayList<>();

        for (LocalDate day : allDays) {
            TblAttendanceDetail detail = detailMap.get(day);
            String status;
            String statusLabel;

            if (detail == null) {
                // ✅ No record = week off or not assigned
                status      = "W";
                statusLabel = "Week Off";
                weekOffDays++;
            } else {
                // ✅ Resolve status from detail fields
                status      = resolveStatus(detail);
                statusLabel = statusLabel(status);

                switch (status) {
                    case "P"  -> presentDays++;
                    case "A"  -> absentDays++;
                    case "L"  -> { presentDays++; lateDays++; }
                    case "H"  -> holidayDays++;
                    case "W"  -> weekOffDays++;
                }
            }

            dailyStatus.add(DailyStatusResponse.builder()
                .date(day.toString())
                .dayName(day.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .status(status)
                .statusLabel(statusLabel)
                .build()
            );
        }

        // ✅ Working days = all days - week offs
        int totalDays = (int) allDays.stream()
            .filter(d -> detailMap.containsKey(d))
            .count();
        if (totalDays == 0) totalDays = allDays.size();

        int pct = totalDays > 0
            ? (presentDays * 100 / totalDays) : 0;

        return EmployeeWeeklyDetailResponse.builder()
            .empCode(empCode)
            .empName(emp != null ? emp.getName()     : empCode)
            .department(emp != null ? emp.getDeptCode() : "")
            .designation(emp != null ? emp.getDesgCode() : "")
            .shiftCode(shiftCode)
            .shiftName(shift != null ? shift.getShiftName() : shiftCode)
            .weekStart(weekStartStr)
            .weekEnd(weekEndStr)
            .presentDays(presentDays)
            .absentDays(absentDays)
            .lateDays(lateDays)
            .holidayDays(holidayDays)
            .weekOffDays(weekOffDays)
            .totalDays(totalDays)
            .attendancePercent(pct)
            .dailyStatus(dailyStatus)
            .build();
    }

    // ─────────────────────────────────────
    // ✅ Build single shift weekly summary
    // ─────────────────────────────────────
    private WeeklyShiftSummaryResponse buildShiftWeeklySummary(
        String btCode, String companyCode,
        TblShift shift,
        LocalDate weekStart, LocalDate weekEnd,
        List<LocalDate> workDays
    ) {
        // ✅ Get all headers for this shift in week
        List<TblAttendance> headers = attendanceRepo
            .findByBtCodeAndCompanyCodeAndShiftCodeAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                btCode, companyCode,
                shift.getShiftCode(),
                weekStart, weekEnd
            );

        // ✅ Sum up counts from headers
        int totalPresent = headers.stream()
            .mapToInt(h -> h.getPresentCount() != null
                ? h.getPresentCount().intValue() : 0
            ).sum();

        int totalAbsent = headers.stream()
            .mapToInt(h -> {
                int a = h.getAbsentCount()  != null ? h.getAbsentCount()  : 0;
                int l = h.getLeaveCount()   != null ? h.getLeaveCount()   : 0;
                return a + l;
            }).sum();

        // ✅ Total employees from first submitted header
        int totalEmployees = headers.stream()
            .mapToInt(h -> h.getTotalEmployees() != null
                ? h.getTotalEmployees() : 0
            ).max().orElse(0);

        int submittedDays = headers.size();

        return WeeklyShiftSummaryResponse.builder()
            .shiftCode(shift.getShiftCode())
            .shiftName(shift.getShiftName())
            .startTime(shift.getStartTime().toString().substring(0, 5))
            .endTime(shift.getEndTime().toString().substring(0, 5))
            .totalEmployees(totalEmployees)
            .totalPresent(totalPresent)
            .totalAbsent(totalAbsent)
            .submittedDays(submittedDays)
            .totalDays(workDays.size())
            .build();
    }

    // ─────────────────────────────────────
    // ✅ Resolve status from detail
    // ─────────────────────────────────────
    private String resolveStatus(TblAttendanceDetail d) {
        if (d.getDayPlanStatus() != null) {
            return switch (d.getDayPlanStatus()) {
                case 2  -> "W";     // Week off
                case 3  -> "A";     // Absent / Leave
                case 4  -> "H";     // Holiday
                default -> {
                    // dayPlanStatus = 1 → check presentCount
                    if (d.getPresentCount() != null) {
                        if (d.getPresentCount() >= 1.0) yield "P";
                        if (d.getPresentCount() > 0.0)  yield "L";
                    }
                    yield "A";
                }
            };
        }
        return "A";
    }

    // ─────────────────────────────────────
    // ✅ Status label helper
    // ─────────────────────────────────────
    private String statusLabel(String status) {
        return switch (status) {
            case "P"  -> "Present";
            case "A"  -> "Absent";
            case "L"  -> "Late";
            case "H"  -> "Holiday";
            case "W"  -> "Week Off";
            default   -> status;
        };
    }

    // ─────────────────────────────────────
    // ✅ Get working days (Mon–Sat)
    // ─────────────────────────────────────
    private List<LocalDate> getWorkDays(
        LocalDate start, LocalDate end
    ) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            if (current.getDayOfWeek().getValue() != 7) { // skip Sunday
                days.add(current);
            }
            current = current.plusDays(1);
        }
        return days;
    }

    // ─────────────────────────────────────
    // ✅ Get all days (Mon–Sun)
    // ─────────────────────────────────────
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
}