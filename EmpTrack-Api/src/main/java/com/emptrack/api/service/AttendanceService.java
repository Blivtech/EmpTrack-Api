package com.emptrack.api.service;


import com.emptrack.api.dto.AttendanceDetailResponse;
import com.emptrack.api.dto.AttendanceRequest;
import com.emptrack.api.dto.AttendanceResponse;
import com.emptrack.api.dto.ShiftStatusResponse;
import com.emptrack.api.model.TblAttendance;
import com.emptrack.api.model.TblAttendanceDetail;
import com.emptrack.api.model.TblShift;
import com.emptrack.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final AttendanceDetailRepository detailRepo;
    private final EmployeeRepository employeeRepo;
    private final ShiftRepository shiftRepo;

    // ─────────────────────────────────────────
    // ✅ Generate attendance ID
    // ─────────────────────────────────────────
    private String generateAttendanceId(
        String btCode,
        String shiftCode,
        LocalDate date
    ) {
        String dateStr = date.format(
            DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        return String.format("ATT-%s-%s-%s", btCode, dateStr, shiftCode);
    }

    // ✅ Generate detail ID
    private String generateDetailId(
        String attendanceId,
        String empCode
    ) {
        return String.format("%s-%s", attendanceId, empCode);
    }

    // ─────────────────────────────────────────
    // ✅ Mark attendance (POST)
    // ─────────────────────────────────────────
    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest req) {

        // ✅ Only allow today
        if (!req.getAttendanceDate().equals(LocalDate.now())) {
            throw new RuntimeException(
                "Attendance can only be marked for today"
            );
        }

        // ✅ Generate attendance ID
        String attendanceId = generateAttendanceId(
            req.getBtCode(),
            req.getShiftCode(),
            req.getAttendanceDate()
        );

        // ✅ Check if already exists
        Optional<TblAttendance> existing =
            attendanceRepo.findByAttendanceId(attendanceId);

        if (existing.isPresent()) {
            throw new RuntimeException(
                "Attendance already marked! Use update API. " +
                "attendanceId: " + attendanceId
            );
        }

        // ✅ Calculate summary
        double presentCount = 0;
        int absentCount     = 0;
        int weekoffCount    = 0;
        int leaveCount      = 0;
        int holidayCount    = 0;

        for (var emp : req.getEmployees()) {
            switch (emp.getDayPlanStatus()) {
                case 1 -> presentCount += emp.getPresentCount();
                case 2 -> weekoffCount++;
                case 3 -> { absentCount++; leaveCount++; }
                case 4 -> holidayCount++;
            }
        }

        // ✅ Save attendance header
        TblAttendance attendance = TblAttendance.builder()
            .attendanceId(attendanceId)
            .btCode(req.getBtCode())
            .companyCode(req.getCompanyCode())
            .shiftCode(req.getShiftCode())
            .attendanceDate(req.getAttendanceDate())
            .totalEmployees(req.getEmployees().size())
            .presentCount(presentCount)
            .absentCount(absentCount)
            .weekoffCount(weekoffCount)
            .leaveCount(leaveCount)
            .holidayCount(holidayCount)
            .markedBy(req.getMarkedBy())
            .build();

        attendanceRepo.save(attendance);

        // ✅ Save employee details
        List<TblAttendanceDetail> details = req.getEmployees().stream()
            .map(emp -> TblAttendanceDetail.builder()
                .detailId(generateDetailId(attendanceId, emp.getEmpCode()))
                .attendanceId(attendanceId)
                .btCode(req.getBtCode())
                .companyCode(req.getCompanyCode())
                .shiftCode(req.getShiftCode())
                .empCode(emp.getEmpCode())
                .attendanceDate(req.getAttendanceDate())
                .dayPlanStatus(emp.getDayPlanStatus())
                .workType(emp.getWorkType() != null ? emp.getWorkType() : 1)
                .presentCount(emp.getPresentCount() != null ? emp.getPresentCount() : 0.0)
                .absentCount(emp.getAbsentCount() != null ? emp.getAbsentCount() : 0)
                .remarks(emp.getRemarks() != null ? emp.getRemarks() : "")
                .markedBy(req.getMarkedBy())
                .build()
            ).toList();

        detailRepo.saveAll(details);

        return AttendanceResponse.builder()
            .attendanceId(attendanceId)
            .companyCode(req.getCompanyCode())
            .shiftCode(req.getShiftCode())
            .attendanceDate(req.getAttendanceDate())
            .totalEmployees(req.getEmployees().size())
            .presentCount(presentCount)
            .absentCount(absentCount)
            .weekoffCount(weekoffCount)
            .leaveCount(leaveCount)
            .holidayCount(holidayCount)
            .isMarked(true)
            .mode("NEW")
            .build();
    }


    @Transactional
    public AttendanceResponse updateAttendance(
            String attendanceId,
            AttendanceRequest req
    ) {
        if (!req.getAttendanceDate().equals(LocalDate.now())) {
            throw new RuntimeException(
                    "Attendance can only be edited for today"
            );
        }

        TblAttendance existing = attendanceRepo
                .findByAttendanceId(attendanceId)
                .orElseThrow(() -> new RuntimeException(
                        "Attendance not found: " + attendanceId
                ));


        detailRepo.deleteByAttendanceId(attendanceId);
        detailRepo.flush();     // ← force DELETE now

        // ✅ Step 2 — Delete old header
        attendanceRepo.delete(existing);
        attendanceRepo.flush(); // ← force DELETE now

        // ✅ Step 3 — Recalculate counts
        double presentCount = 0;
        int absentCount     = 0;
        int weekoffCount    = 0;
        int leaveCount      = 0;
        int holidayCount    = 0;

        for (var emp : req.getEmployees()) {
            switch (emp.getDayPlanStatus()) {
                case 1 -> presentCount += emp.getPresentCount();
                case 2 -> weekoffCount++;
                case 3 -> { absentCount++; leaveCount++; }
                case 4 -> holidayCount++;
            }
        }

        // ✅ Step 4 — Recreate header with same attendanceId
        TblAttendance newHeader = TblAttendance.builder()
                .attendanceId(attendanceId)     // ← same ID
                .btCode(req.getBtCode())
                .companyCode(req.getCompanyCode())
                .shiftCode(req.getShiftCode())
                .attendanceDate(req.getAttendanceDate())
                .totalEmployees(req.getEmployees().size())
                .presentCount(presentCount)
                .absentCount(absentCount)
                .weekoffCount(weekoffCount)
                .leaveCount(leaveCount)
                .holidayCount(holidayCount)
                .markedBy(req.getMarkedBy())
                .status(1)
                .build();

        attendanceRepo.save(newHeader);
        attendanceRepo.flush(); // ← force INSERT now

        // ✅ Step 5 — Recreate details
        List<TblAttendanceDetail> details = req.getEmployees().stream()
                .map(emp -> TblAttendanceDetail.builder()
                        .detailId(generateDetailId(attendanceId, emp.getEmpCode()))
                        .attendanceId(attendanceId)
                        .btCode(req.getBtCode())
                        .companyCode(req.getCompanyCode())
                        .shiftCode(req.getShiftCode())
                        .empCode(emp.getEmpCode())
                        .attendanceDate(req.getAttendanceDate())
                        .dayPlanStatus(emp.getDayPlanStatus())
                        .workType(emp.getWorkType()      != null ? emp.getWorkType()      : 1)
                        .presentCount(emp.getPresentCount() != null ? emp.getPresentCount() : 0.0)
                        .absentCount(emp.getAbsentCount()   != null ? emp.getAbsentCount()  : 0)
                        .remarks(emp.getRemarks()        != null ? emp.getRemarks()        : "")
                        .markedBy(req.getMarkedBy())
                        .build()
                ).toList();

        detailRepo.saveAll(details);

        return AttendanceResponse.builder()
                .attendanceId(attendanceId)
                .companyCode(req.getCompanyCode())
                .shiftCode(req.getShiftCode())
                .attendanceDate(req.getAttendanceDate())
                .totalEmployees(req.getEmployees().size())
                .presentCount(presentCount)
                .absentCount(absentCount)
                .weekoffCount(weekoffCount)
                .leaveCount(leaveCount)
                .holidayCount(holidayCount)
                .isMarked(true)
                .mode("EDIT")
                .build();
    }

    // ─────────────────────────────────────────
    // ✅ Get attendance by attendanceId (GET)
    // ─────────────────────────────────────────
    public AttendanceResponse getByAttendanceId(String attendanceId) {

        TblAttendance att = attendanceRepo
            .findByAttendanceId(attendanceId)
            .orElseThrow(() -> new RuntimeException(
                "Attendance not found: " + attendanceId
            ));

        List<TblAttendanceDetail> details =
            detailRepo.findByAttendanceId(attendanceId);

        List<AttendanceDetailResponse> empResponses = details.stream()
            .map(d -> {
                var emp = employeeRepo.findByEmpCode(d.getEmpCode());
                return AttendanceDetailResponse.builder()
                    .detailId(d.getDetailId())
                        .empCode(d.getEmpCode())
                    .name(emp != null ? emp.getName() : "")
                    .dayPlanStatus(d.getDayPlanStatus())
                    .workType(d.getWorkType())
                    .presentCount(d.getPresentCount())
                    .absentCount(d.getAbsentCount())
                    .remarks(d.getRemarks())
                    .build();
            }).toList();

        return AttendanceResponse.builder()
            .attendanceId(att.getAttendanceId())
            .companyCode(att.getCompanyCode())
            .shiftCode(att.getShiftCode())
            .attendanceDate(att.getAttendanceDate())
            .totalEmployees(att.getTotalEmployees())
            .presentCount(att.getPresentCount())
            .absentCount(att.getAbsentCount())
            .weekoffCount(att.getWeekoffCount())
            .leaveCount(att.getLeaveCount())
            .holidayCount(att.getHolidayCount())
            .isMarked(true)
            .mode("EDIT")
            .employees(empResponses)
            .build();
    }

    // ─────────────────────────────────────────
    // ✅ Check attendance exists (GET)
    // ─────────────────────────────────────────
    public Map<String, Object> checkAttendance(
        String btCode,
        String companyId,
        String shiftId,
        LocalDate date
    ) {
        String attendanceId = generateAttendanceId(btCode, shiftId, date);
        Optional<TblAttendance> existing =
            attendanceRepo.findByAttendanceId(attendanceId);

        Map<String, Object> result = new HashMap<>();
        result.put("isMarked", existing.isPresent());
        result.put("attendanceId", existing.isPresent() ? attendanceId : null);
        result.put("mode", existing.isPresent() ? "EDIT" : "NEW");
        return result;
    }

    // ─────────────────────────────────────────
    // ✅ Get today's all shifts status (GET)
    // ─────────────────────────────────────────
    public List<ShiftStatusResponse> getTodayStatus(
        String btCode,
        String companyCode,
        LocalDate date
    ) {
        // ✅ Get all shifts for this company
        List<TblShift> shifts = shiftRepo
            .findByBtCodeAndCompanyCode(btCode, companyCode);

        // ✅ Get all attendance records for today
        List<TblAttendance> todayAttendance =
            attendanceRepo.findByBtCodeAndCompanyCodeAndAttendanceDate(
                btCode, companyCode, date
            );

        return shifts.stream().map(shift -> {
            Optional<TblAttendance> att = todayAttendance.stream()
                .filter(a -> a.getShiftCode().equals(shift.getShiftCode()))
                .findFirst();

            return ShiftStatusResponse.builder()
                .shiftCode(shift.getShiftCode())
                .shiftName(shift.getShiftName())
                .attendanceId(att.map(TblAttendance::getAttendanceId).orElse(null))
                .isMarked(att.isPresent())
                .presentCount(att.map(TblAttendance::getPresentCount).orElse(0.0))
                .absentCount(att.map(TblAttendance::getAbsentCount).orElse(0))
                .weekoffCount(att.map(TblAttendance::getWeekoffCount).orElse(0))
                .leaveCount(att.map(TblAttendance::getLeaveCount).orElse(0))
                .holidayCount(att.map(TblAttendance::getHolidayCount).orElse(0))
                .mode(att.isPresent() ? "EDIT" : "NEW")
                .build();
        }).toList();
    }
}