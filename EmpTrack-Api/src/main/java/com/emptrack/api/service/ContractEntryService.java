package com.emptrack.api.service;


import com.emptrack.api.dto.ContractEntryRequest;
import com.emptrack.api.dto.ContractEntryResponse;
import com.emptrack.api.dto.ContractSummaryItem;
import com.emptrack.api.dto.ContractSummaryResponse;
import com.emptrack.api.model.TblContractEntry;
import com.emptrack.api.repository.ContractEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractEntryService {

    private final ContractEntryRepository entryRepository;

    // ✅ Add entries
    @Transactional
    public void addEntries(ContractEntryRequest req) {
        LocalDate entryDate = LocalDate.parse(req.getEntryDate());
        AtomicInteger index = new AtomicInteger(1);

        req.getEntries().forEach(detail -> {
            String entryId = String.format("CE-%s-%s-%s-%03d",
                req.getBtCode(),
                req.getEntryDate().replace("-", ""),
                req.getShiftCode(),
                index.getAndIncrement()
            );

            TblContractEntry entry = new TblContractEntry();
            entry.setEntryId(entryId);
            entry.setBtCode(req.getBtCode());
            entry.setCompanyCode(req.getCompanyCode());
            entry.setShiftCode(req.getShiftCode());
            entry.setShiftName(req.getShiftName());
            entry.setEntryDate(entryDate);
            entry.setProductId(detail.getProductId());
            entry.setProductName(detail.getProductName());
            entry.setWorkName(detail.getWorkName());
            entry.setQuantityDone(detail.getQuantityDone());
            entry.setRatePerUnit(detail.getRatePerUnit());  // ✅ Snapshot
            entry.setTotalAmount(
                detail.getQuantityDone() * detail.getRatePerUnit()
            );
            entry.setUnit(detail.getUnit());
            entry.setStatus(1);
            entryRepository.save(entry);
        });
    }

    // ✅ Get by month
    public List<ContractEntryResponse> getByMonth(
        String btCode, String companyCode, String month
    ) {
        return entryRepository
            .findByMonth(btCode, companyCode, month)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ✅ Get by date + shift
    public List<ContractEntryResponse> getByDateShift(
        String btCode, String companyCode,
        String date, String shiftCode
    ) {
        return entryRepository
            .findByBtCodeAndCompanyCodeAndEntryDateAndShiftCode(
                btCode, companyCode,
                LocalDate.parse(date), shiftCode
            )
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ✅ Monthly summary
    public ContractSummaryResponse getMonthlySummary(
        String btCode, String companyCode, String month
    ) {
        List<TblContractEntry> entries =
            entryRepository.findByMonth(btCode, companyCode, month);

        Double totalAmount = entries.stream()
            .mapToDouble(TblContractEntry::getTotalAmount).sum();

        // ✅ Group by product + work
        Map<String, List<TblContractEntry>> grouped = entries.stream()
            .collect(Collectors.groupingBy(e ->
                e.getProductId() + "_" + e.getWorkName()
            ));

        List<ContractSummaryItem> breakdown = grouped.values().stream()
            .map(group -> {
                TblContractEntry first = group.get(0);
                Double totalQty = group.stream()
                    .mapToDouble(TblContractEntry::getQuantityDone).sum();
                Double groupAmount = group.stream()
                    .mapToDouble(TblContractEntry::getTotalAmount).sum();

                return ContractSummaryItem.builder()
                    .productId(first.getProductId())
                    .productName(first.getProductName())
                    .workName(first.getWorkName())
                    .totalQty(totalQty)
                    .ratePerUnit(first.getRatePerUnit())
                    .totalAmount(groupAmount)
                    .unit(first.getUnit())
                    .build();
            })
            .collect(Collectors.toList());

        return ContractSummaryResponse.builder()
            .month(month)
            .btCode(btCode)
            .companyCode(companyCode)
            .totalAmount(totalAmount)
            .breakdown(breakdown)
            .build();
    }

    // ✅ Delete entry
    public void deleteEntry(String entryId) {
        TblContractEntry entry = entryRepository
            .findByEntryId(entryId)
            .orElseThrow(() -> new RuntimeException(
                "Entry not found: " + entryId
            ));
        entryRepository.delete(entry);
    }

    // ─────────────────────────────────────
    // Helper
    // ─────────────────────────────────────
    private ContractEntryResponse toResponse(TblContractEntry e) {
        return ContractEntryResponse.builder()
            .entryId(e.getEntryId())
            .btCode(e.getBtCode())
            .companyCode(e.getCompanyCode())
            .shiftCode(e.getShiftCode())
            .shiftName(e.getShiftName())
            .entryDate(e.getEntryDate().toString())
            .productId(e.getProductId())
            .productName(e.getProductName())
            .workName(e.getWorkName())
            .quantityDone(e.getQuantityDone())
            .ratePerUnit(e.getRatePerUnit())
            .totalAmount(e.getTotalAmount())
            .unit(e.getUnit())
            .status(e.getStatus())
            .createdAt(e.getCreatedAt().toString())
            .build();
    }
}