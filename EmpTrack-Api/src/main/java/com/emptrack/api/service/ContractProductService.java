package com.emptrack.api.service;


import com.emptrack.api.dto.ContractProductRequest;
import com.emptrack.api.dto.ContractProductResponse;
import com.emptrack.api.dto.ContractRateHistoryResponse;
import com.emptrack.api.dto.UpdateContractRateRequest;
import com.emptrack.api.model.TblContractProduct;
import com.emptrack.api.model.TblContractRateHistory;
import com.emptrack.api.repository.ContractProductRepository;
import com.emptrack.api.repository.ContractRateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractProductService {

    private final ContractProductRepository productRepository;
    private final ContractRateHistoryRepository historyRepository;

    // ✅ Add product
    @Transactional
    public void addProduct(ContractProductRequest req) {
        String productId = String.format("CP-%s-%d",
            req.getBtCode(), System.currentTimeMillis()
        );

        TblContractProduct product = new TblContractProduct();
        product.setProductId(productId);
        product.setBtCode(req.getBtCode());
        product.setCompanyCode(req.getCompanyCode());
        product.setProductName(req.getProductName());
        product.setWorkName(req.getWorkName());
        product.setRatePerUnit(req.getRatePerUnit());
        product.setUnit(req.getUnit() != null ? req.getUnit() : "pcs");
        product.setColorTag(req.getColorTag() != null ? req.getColorTag() : "#1565C0");
        product.setStatus(1);
        productRepository.save(product);

        // ✅ Save initial rate as history
        saveHistory(
            productId,
            req.getProductName(),
            req.getWorkName(),
            req.getBtCode(),
            req.getCompanyCode(),
            0.0,
            req.getRatePerUnit(),
            req.getUnit(),
            req.getBtCode(),
            "Initial rate",
            LocalDate.now()
        );
    }

    // ✅ Get all products
    public List<ContractProductResponse> getProducts(
        String btCode, String companyCode
    ) {
        return productRepository
            .findByBtCodeAndCompanyCodeAndStatusOrderByCreatedAtDesc(
                btCode, companyCode, 1
            )
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ✅ Update product
    @Transactional
    public void updateProduct(String productId, ContractProductRequest req) {
        TblContractProduct product = productRepository
            .findByProductId(productId)
            .orElseThrow(() -> new RuntimeException(
                "Product not found: " + productId
            ));

        product.setProductName(req.getProductName());
        product.setWorkName(req.getWorkName());
        if (req.getColorTag() != null) product.setColorTag(req.getColorTag());
        productRepository.save(product);
    }

    // ✅ Update rate + save history
    @Transactional
    public void updateRate(String productId, UpdateContractRateRequest req) {
        TblContractProduct product = productRepository
            .findByProductId(productId)
            .orElseThrow(() -> new RuntimeException(
                "Product not found: " + productId
            ));

        Double oldRate = product.getRatePerUnit();

        // ✅ Save history
        saveHistory(
            productId,
            product.getProductName(),
            product.getWorkName(),
            product.getBtCode(),
            product.getCompanyCode(),
            oldRate,
            req.getNewRate(),
            product.getUnit(),
            req.getChangedBy(),
            req.getReason(),
            LocalDate.parse(req.getEffectiveDate())
        );

        // ✅ Update current rate
        product.setRatePerUnit(req.getNewRate());
        productRepository.save(product);
    }

    // ✅ Delete product
    @Transactional
    public void deleteProduct(String productId) {
        TblContractProduct product = productRepository
            .findByProductId(productId)
            .orElseThrow(() -> new RuntimeException(
                "Product not found: " + productId
            ));
        product.setStatus(2);
        productRepository.save(product);
    }

    // ✅ Get rate history
    public List<ContractRateHistoryResponse> getRateHistory(String productId) {
        return historyRepository
            .findByProductIdOrderByCreatedAtDesc(productId)
            .stream()
            .map(h -> ContractRateHistoryResponse.builder()
                .historyId(h.getHistoryId())
                .productId(h.getProductId())
                .productName(h.getProductName())
                .workName(h.getWorkName())
                .oldRate(h.getOldRate())
                .newRate(h.getNewRate())
                .unit(h.getUnit())
                .changedBy(h.getChangedBy())
                .reason(h.getReason())
                .effectiveDate(h.getEffectiveDate().toString())
                .createdAt(h.getCreatedAt().toString())
                .build()
            )
            .collect(Collectors.toList());
    }

    // ─────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────

    private void saveHistory(
        String productId, String productName, String workName,
        String btCode, String companyCode,
        Double oldRate, Double newRate,
        String unit, String changedBy, String reason,
        LocalDate effectiveDate
    ) {
        String historyId = String.format("CRH-%s-%s-%s",
            btCode, productId,
            LocalDate.now().toString().replace("-", "")
        );

        TblContractRateHistory history = new TblContractRateHistory();
        history.setHistoryId(historyId);
        history.setBtCode(btCode);
        history.setCompanyCode(companyCode);
        history.setProductId(productId);
        history.setProductName(productName);
        history.setWorkName(workName);
        history.setOldRate(oldRate);
        history.setNewRate(newRate);
        history.setUnit(unit);
        history.setChangedBy(changedBy);
        history.setReason(reason);
        history.setEffectiveDate(effectiveDate);
        historyRepository.save(history);
    }

    private ContractProductResponse toResponse(TblContractProduct p) {
        return ContractProductResponse.builder()
            .productId(p.getProductId())
            .btCode(p.getBtCode())
            .companyCode(p.getCompanyCode())
            .productName(p.getProductName())
            .workName(p.getWorkName())
            .ratePerUnit(p.getRatePerUnit())
            .unit(p.getUnit())
            .colorTag(p.getColorTag())
            .status(p.getStatus())
            .createdAt(p.getCreatedAt().toString())
            .build();
    }
}