package com.emptrack.api.service;

import com.emptrack.api.dto.WorkEntryRequest;
import com.emptrack.api.dto.WorkEntryResponse;
import com.emptrack.api.dto.WorkSummaryBreakdown;
import com.emptrack.api.dto.WorkSummaryResponse;
import com.emptrack.api.model.TblProduct;
import com.emptrack.api.model.TblWorkEntry;
import com.emptrack.api.model.TblWorkType;
import com.emptrack.api.repository.EmployeeRepository;
import com.emptrack.api.repository.ProductRepository;
import com.emptrack.api.repository.WorkEntryRepository;
import com.emptrack.api.repository.WorkTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkEntryService {

    private final WorkEntryRepository workEntryRepository;
    private final ProductRepository productRepository;
    private final WorkTypeRepository workTypeRepository;
    private final EmployeeRepository employeeRepository;

    // ✅ Add work entries (multiple rows)
    @Transactional
    public void addWorkEntries(WorkEntryRequest req) {
        LocalDate entryDate = LocalDate.parse(req.getEntryDate());
        AtomicInteger index = new AtomicInteger(1);

        req.getEntries().forEach(detail -> {
            String entryId = String.format("WRK-%s-%s-%s-%03d",
                req.getBtCode(),
                req.getEmpCode(),
                req.getEntryDate().replace("-", ""),
                index.getAndIncrement()
            );

            TblWorkEntry entry = new TblWorkEntry();
            entry.setEntryId(entryId);
            entry.setBtCode(req.getBtCode());
            entry.setCompanyCode(req.getCompanyCode());
            entry.setEmpCode(req.getEmpCode());
            entry.setProductId(detail.getProductId());
            entry.setWorkTypeId(detail.getWorkTypeId());
            entry.setEntryDate(entryDate);
            entry.setPiecesDone(detail.getPiecesDone());

            // ✅ Snapshot rate at time of entry
            entry.setRatePerPiece(detail.getRatePerPiece());
            entry.setTotalAmount(detail.getPiecesDone() * detail.getRatePerPiece());

            entry.setRemarks(detail.getRemarks());
            entry.setStatus(1);
            workEntryRepository.save(entry);
        });
    }

    // ✅ Get entries by date
    public List<WorkEntryResponse> getByDate(
        String btCode, String companyCode, String date
    ) {
        LocalDate entryDate = LocalDate.parse(date);
        return workEntryRepository
            .findByBtCodeAndCompanyCodeAndEntryDateOrderByCreatedAtDesc(
                btCode, companyCode, entryDate
            )
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ✅ Get entries by month
    public List<WorkEntryResponse> getByMonth(
        String btCode, String companyCode, String month
    ) {
        return workEntryRepository
            .findByMonth(btCode, companyCode, month)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ✅ Monthly summary — grouped by employee
    public List<WorkSummaryResponse> getMonthlySummary(
        String btCode, String companyCode, String month
    ) {
        List<TblWorkEntry> entries = workEntryRepository
            .findByMonth(btCode, companyCode, month);

        // ✅ Group by empCode
        Map<String, List<TblWorkEntry>> byEmp = entries.stream()
            .collect(Collectors.groupingBy(TblWorkEntry::getEmpCode));

        return byEmp.entrySet().stream().map(empEntry -> {
            String empCode = empEntry.getKey();
            List<TblWorkEntry> empEntries = empEntry.getValue();

            // ✅ Get emp name
            String empName = employeeRepository
                .findByEmpCode(empCode).getName();

            // ✅ Total pieces + amount
            Double totalPieces = empEntries.stream()
                .mapToDouble(TblWorkEntry::getPiecesDone).sum();
            Double totalAmount = empEntries.stream()
                .mapToDouble(TblWorkEntry::getTotalAmount).sum();

            // ✅ Breakdown by product + work type
            Map<String, List<TblWorkEntry>> byWorkType = empEntries.stream()
                .collect(Collectors.groupingBy(e ->
                    e.getProductId() + "_" + e.getWorkTypeId()
                ));

            List<WorkSummaryBreakdown> breakdown = byWorkType.values()
                .stream().map(group -> {
                    TblWorkEntry first = group.get(0);

                    // ✅ Get product name
                    String productName = productRepository
                        .findByProductId(first.getProductId())
                        .map(TblProduct::getProductName)
                        .orElse(first.getProductId());

                    // ✅ Get work type name
                    String workTypeName = workTypeRepository
                        .findByWorkTypeId(first.getWorkTypeId())
                        .map(TblWorkType::getWorkTypeName)
                        .orElse(first.getWorkTypeId());

                    Double bPieces = group.stream()
                        .mapToDouble(TblWorkEntry::getPiecesDone).sum();
                    Double bAmount = group.stream()
                        .mapToDouble(TblWorkEntry::getTotalAmount).sum();

                    return WorkSummaryBreakdown.builder()
                        .productId(first.getProductId())
                        .productName(productName)
                        .workTypeId(first.getWorkTypeId())
                        .workTypeName(workTypeName)
                        .totalPieces(bPieces)
                        .ratePerPiece(first.getRatePerPiece())
                        .totalAmount(bAmount)
                        .build();
                })
                .collect(Collectors.toList());

            return WorkSummaryResponse.builder()
                .empCode(empCode)
                .empName(empName)
                .totalPieces(totalPieces)
                .totalAmount(totalAmount)
                .breakdown(breakdown)
                .build();

        }).collect(Collectors.toList());
    }

    // ✅ Delete entry
    public void deleteEntry(String entryId) {
        TblWorkEntry entry = workEntryRepository.findByEntryId(entryId)
            .orElseThrow(() -> new RuntimeException("Entry not found: " + entryId));
        workEntryRepository.delete(entry);
    }

    // ─────────────────────────────────────
    // Helper
    // ─────────────────────────────────────
    private WorkEntryResponse toResponse(TblWorkEntry entry) {
        String empName = employeeRepository
            .findByEmpCode(entry.getEmpCode()).getName();

        String productName = productRepository
            .findByProductId(entry.getProductId())
            .map(TblProduct::getProductName)
            .orElse(entry.getProductId());

        String workTypeName = workTypeRepository
            .findByWorkTypeId(entry.getWorkTypeId())
            .map(TblWorkType::getWorkTypeName)
            .orElse(entry.getWorkTypeId());

        return WorkEntryResponse.builder()
            .entryId(entry.getEntryId())
            .btCode(entry.getBtCode())
            .companyCode(entry.getCompanyCode())
            .empCode(entry.getEmpCode())
            .empName(empName)
            .productId(entry.getProductId())
            .productName(productName)
            .workTypeId(entry.getWorkTypeId())
            .workTypeName(workTypeName)
            .entryDate(entry.getEntryDate().toString())
            .piecesDone(entry.getPiecesDone())
            .ratePerPiece(entry.getRatePerPiece())
            .totalAmount(entry.getTotalAmount())
            .remarks(entry.getRemarks())
            .status(entry.getStatus())
            .createdAt(entry.getCreatedAt().toString())
            .build();
    }
}