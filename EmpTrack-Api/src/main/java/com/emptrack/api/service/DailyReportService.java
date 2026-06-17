package com.emptrack.api.service;

import com.emptrack.api.dto.*;
import com.emptrack.api.model.*;
import com.emptrack.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyReportService {

    private final AttendanceReportRepository       attendanceRepo;
    private final AttendanceDetailReportRepository detailRepo;
    private final ShiftRepository                  shiftRepo;
    private final EmployeeRepository               employeeRepo;
    private final DepartmentRepository   departmentRepo;
    private final DesignationRepository  designationRepo;
    // ─────────────────────────────────────
    // ✅ Get full daily summary — all shifts
    // ─────────────────────────────────────
    public DailyReportSummaryResponse getDailySummary(
        String btCode,
        String companyCode,
        String date
    ) {
        LocalDate attendanceDate = LocalDate.parse(date);

        // ✅ Get all company shifts
        List<TblShift> shifts = shiftRepo
            .findByCompanyCodeAndStatus(companyCode, 1);

        // ✅ Build per-shift summary from tbl_attendance header
        List<ShiftAttendanceSummaryResponse> summaries = shifts.stream()
            .map(shift -> buildShiftSummary(
                btCode, companyCode, attendanceDate, shift
            ))
            .collect(Collectors.toList());

        // ✅ Totals from header rows directly
        int totalPresent    = summaries.stream().mapToInt(
            s -> s.getPresentCount()
        ).sum();
        int totalLeave      = summaries.stream().mapToInt(
            s -> s.getLeaveCount()
        ).sum();
        int totalEmployees  = summaries.stream().mapToInt(
            s -> s.getTotalCount()
        ).sum();

        int submittedShifts = (int) summaries.stream()
            .filter(s -> s.getSubmittedAt() != null).count();
        int pendingShifts   = summaries.size() - submittedShifts;

        return DailyReportSummaryResponse.builder()
            .btCode(btCode)
            .companyCode(companyCode)
            .attendanceDate(date)
            .totalPresent(totalPresent)
            .totalLeave(totalLeave)
            .totalEmployees(totalEmployees)
            .submittedShifts(submittedShifts)
            .pendingShifts(pendingShifts)
            .shifts(summaries)
            .build();
    }

    // ─────────────────────────────────────
    // ✅ Build single shift summary
    //    Uses tbl_attendance header directly
    // ─────────────────────────────────────
    private ShiftAttendanceSummaryResponse buildShiftSummary(
        String btCode, String companyCode,
        LocalDate date, TblShift shift
    ) {
        Optional<TblAttendance> header = attendanceRepo
            .findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCode(
                btCode, companyCode, date, shift.getShiftCode()
            );

        if (header.isEmpty()) {
            // ✅ Not submitted yet — return empty card
            return ShiftAttendanceSummaryResponse.builder()
                .shiftCode(shift.getShiftCode())
                .shiftName(shift.getShiftName())
                .startTime(shift.getStartTime().toString().substring(0, 5))
                .endTime(shift.getEndTime().toString().substring(0, 5))
                .attendanceDate(date.toString())
                .submittedAt(null)          // ← null = pending
                .presentCount(0)
                .holidayCount(0)
                .weekOffCount(0)
                .leaveCount(0)
                .totalCount(0)
                    .build();
        }

        TblAttendance att = header.get();

        // ✅ present_count from header (already calculated on submission)
        int presentCount = att.getPresentCount() != null
            ? att.getPresentCount().intValue() : 0;

        // ✅ leave = absent + leave + holiday
        int leaveCount = 0;
        if (att.getLeaveCount()   != null) leaveCount += att.getLeaveCount();
        int holidayCount  = att.getHolidayCount()  != null
                ? att.getHolidayCount() : 0;
        int weekOffCount  = att.getWeekoffCount()  != null
                ? att.getWeekoffCount() : 0;
        int totalCount = att.getTotalEmployees() != null
            ? att.getTotalEmployees() : 0;

        // ✅ submitted time = created_at of header
        String submittedAt = att.getCreatedAt() != null
            ? att.getCreatedAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
              )
            : null;

        return ShiftAttendanceSummaryResponse.builder()
            .shiftCode(shift.getShiftCode())
            .shiftName(shift.getShiftName())
            .startTime(shift.getStartTime().toString().substring(0, 5))
            .endTime(shift.getEndTime().toString().substring(0, 5))
            .attendanceDate(date.toString())
            .submittedAt(submittedAt)
            .presentCount(presentCount)
            .leaveCount(leaveCount)
             .holidayCount(holidayCount)
             .weekOffCount(weekOffCount)
            .totalCount(totalCount)
            .build();
    }

    // ─────────────────────────────────────
    // ✅ Get employees — by shift + type
    //    Uses tbl_attendance_detail rows
    // ─────────────────────────────────────

    // ✅ getEmployeesByShiftAndType — full fix
    public List<AttendanceEmployeeResponse> getEmployeesByShiftAndType(
            String btCode, String companyCode,
            String date, String shiftCode, String type
    ) {
        LocalDate attendanceDate = LocalDate.parse(date);

        // ✅ Fetch detail rows by type
        List<TblAttendanceDetail> details;
        if (type.equals("PRESENT")) {
            details = detailRepo
                    .findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCodeAndPresentCountGreaterThan(
                            btCode, companyCode, attendanceDate, shiftCode, 0.0
                    );
        } else if (type.equals("ABSENT")) {
            details = detailRepo
                    .findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCodeAndAbsentCount(
                            btCode, companyCode, attendanceDate, shiftCode, 1
                    );
        } else if (type.equals("HOLIDAY_WO")) {
            details = detailRepo
                    .findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCodeAndDayPlanStatusIn(
                            btCode, companyCode, attendanceDate, shiftCode,
                            List.of(2, 3)
                    );
        } else {
            details = detailRepo
                    .findByBtCodeAndCompanyCodeAndAttendanceDateAndShiftCode(
                            btCode, companyCode, attendanceDate, shiftCode
                    );
        }

        // ✅ Batch fetch employees
        List<String> empCodes = details.stream()
                .map(TblAttendanceDetail::getEmpCode)
                .distinct()
                .collect(Collectors.toList());

        Map<String, TblEmployee> empMap = employeeRepo
                .findByEmpCodeIn(empCodes)
                .stream()
                .collect(Collectors.toMap(
                        TblEmployee::getEmpCode, e -> e
                ));

        // ✅ Batch fetch dept names from tbl_departments
        List<String> deptCodes = empMap.values().stream()
                .map(TblEmployee::getDeptCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> deptMap = departmentRepo
                .findByDeptCodeIn(deptCodes)
                .stream()
                .collect(Collectors.toMap(
                        TblDepartment::getDeptCode,
                        TblDepartment::getName     // ✅ name from tbl_departments
                ));

        // ✅ Batch fetch desg names from tbl_designations
        List<String> desgCodes = empMap.values().stream()
                .map(TblEmployee::getDesgCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> desgMap = designationRepo
                .findByDesgCodeIn(desgCodes)
                .stream()
                .collect(Collectors.toMap(
                        TblDesignation::getDesgCode,
                        TblDesignation::getName     // ✅ name from tbl_designations
                ));

        // ✅ Map to response
        return details.stream()
                .map(d -> {
                    TblEmployee emp = empMap.get(d.getEmpCode());
                    String status   = resolveStatus(d);

                    // ✅ Get dept name from deptMap
                    String deptName = "";
                    if (emp != null && emp.getDeptCode() != null) {
                        deptName = deptMap.getOrDefault(
                                emp.getDeptCode(), ""
                        );
                    }

                    // ✅ Get desg name from desgMap
                    String desgName = "";
                    if (emp != null && emp.getDesgCode() != null) {
                        desgName = desgMap.getOrDefault(
                                emp.getDesgCode(), ""
                        );
                    }

                    return AttendanceEmployeeResponse.builder()
                            .empCode(d.getEmpCode())
                            .empName(emp != null ? emp.getName() : d.getEmpCode())
                            .deptCode(emp != null ? emp.getDeptCode() : "")
                            .desgCode(emp != null ? emp.getDesgCode() : "")
                            .deptName(deptName)     // ✅ "Construction"
                            .desgName(desgName)     // ✅ "Supervisor"
                            .status(status)
                            .statusLabel(statusLabel(status))
                            .lateMinutes(0)
                            .build();
                })
                .sorted(Comparator.comparing(
                        AttendanceEmployeeResponse::getEmpName
                ))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────
    // ✅ Resolve status from detail row
    // ─────────────────────────────────────
    private String resolveStatus(TblAttendanceDetail d) {
        if (d.getAbsentCount() != null && d.getAbsentCount() == 1) {
            return "A";     // Absent
        }
        if (d.getPresentCount() != null) {
            if (d.getPresentCount() >= 1.0) return "P";    // Full present
            if (d.getPresentCount() > 0.0)  return "L";    // Late / half day
        }
        if (d.getDayPlanStatus() != null) {
            if (d.getDayPlanStatus() == 3)  return "H";    // Holiday
            if (d.getDayPlanStatus() == 4)  return "WO";   // Week off
        }
        return "A";
    }

    // ─────────────────────────────────────
    // ✅ Total employees for a shift
    //    (from shift plan or employee count)
    // ─────────────────────────────────────
//    private int getTotalEmployeesForShift(
//        String companyCode, String shiftCode
//    ) {
//        return (int) employeeRepo
//            .countByCompanyCodeAndShiftCodeAndStatus(
//                companyCode, shiftCode, 1
//            );
//    }

    // ─────────────────────────────────────
    // ✅ Status label helper
    // ─────────────────────────────────────
    private String statusLabel(String status) {
        return switch (status) {
            case "P"  -> "Present";
            case "A"  -> "Absent";
            case "L"  -> "Late";
            case "H"  -> "Holiday";
            case "WO" -> "Week Off";
            default   -> status;
        };
    }
}