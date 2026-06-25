package com.emptrack.api.service;


import com.emptrack.api.dto.BonusRequest;
import com.emptrack.api.dto.BonusResponse;
import com.emptrack.api.model.TblBonus;
import com.emptrack.api.repository.BonusRepository;
import com.emptrack.api.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BonusService {

    private final BonusRepository bonusRepository;
    private final EmployeeRepository employeeRepository;

    // ✅ Return void
    public void addBonus(BonusRequest req) {
        LocalDate bonusDate = LocalDate.parse(req.getBonusDate());

        String bonusId = String.format("BON-%s-%s-%s",
                req.getBtCode(),
                req.getEmpCode(),
                req.getBonusDate().replace("-", "")+"-"+ System.currentTimeMillis()
        );

        TblBonus bonus = new TblBonus();
        bonus.setBonusId(bonusId);
        bonus.setBtCode(req.getBtCode());
        bonus.setCompanyCode(req.getCompanyCode());
        bonus.setEmpCode(req.getEmpCode());
        bonus.setBonusDate(bonusDate);
        bonus.setBonusType(req.getBonusType());
        bonus.setAmount(req.getAmount());
        bonus.setRemarks(req.getRemarks());
        bonus.setStatus(1);

        bonusRepository.save(bonus);
    }
}