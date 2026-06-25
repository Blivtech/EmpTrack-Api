package com.emptrack.api.service;


import com.emptrack.api.dto.OvertimeRequest;
import com.emptrack.api.dto.OvertimeResponse;
import com.emptrack.api.model.TblEmployee;
import com.emptrack.api.model.TblOvertime;
import com.emptrack.api.model.TblShift;
import com.emptrack.api.repository.EmployeeRepository;
import com.emptrack.api.repository.OvertimeRepository;
import com.emptrack.api.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OvertimeService {

    private final OvertimeRepository overtimeRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;

    // ✅ Return void — no need to return response
    public void addOvertime(OvertimeRequest req) {
        LocalDate otDate = LocalDate.parse(req.getOtDate());

        String otId = String.format("OT-%s-%s-%s",
                req.getBtCode(),
                req.getEmpCode(),
                req.getOtDate().replace("-", "")+"-"+ System.currentTimeMillis()
        );

        TblOvertime ot = new TblOvertime();
        ot.setOtId(otId);
        ot.setBtCode(req.getBtCode());
        ot.setCompanyCode(req.getCompanyCode());
        ot.setEmpCode(req.getEmpCode());
        ot.setOtDate(otDate);
        ot.setShiftCode(req.getShiftCode());
        ot.setOtHours(req.getOtHours());
        ot.setOtAmount(req.getOtAmount());
        ot.setRemarks(req.getRemarks());
        ot.setStatus(1);

        overtimeRepository.save(ot);
    }
    
}