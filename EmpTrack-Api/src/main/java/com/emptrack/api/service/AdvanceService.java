package com.emptrack.api.service;


import com.emptrack.api.dto.AdvanceRequest;
import com.emptrack.api.dto.AdvanceResponse;
import com.emptrack.api.model.TblAdvance;
import com.emptrack.api.model.TblEmployee;
import com.emptrack.api.repository.AdvanceRepository;
import com.emptrack.api.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvanceService {

    private final AdvanceRepository advanceRepository;
    private final EmployeeRepository employeeRepository;

    // ✅ Return void
    public void addAdvance(AdvanceRequest req) {
        LocalDate requestDate = LocalDate.parse(req.getRequestDate());

        String advanceId = String.format("ADV-%s-%s-%s",
                req.getBtCode(),
                req.getEmpCode(),
                req.getRepayMonth().replace("-", "")
        );

        TblAdvance advance = new TblAdvance();
        advance.setAdvanceId(advanceId);
        advance.setBtCode(req.getBtCode());
        advance.setCompanyCode(req.getCompanyCode());
        advance.setEmpCode(req.getEmpCode());
        advance.setRequestDate(requestDate);
        advance.setAmount(req.getAmount());
        advance.setRepayMonth(req.getRepayMonth());
        advance.setRemarks(req.getRemarks());
        advance.setStatus(1);

        advanceRepository.save(advance);
    }


}