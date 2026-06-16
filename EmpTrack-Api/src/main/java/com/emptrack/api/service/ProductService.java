package com.emptrack.api.service;


import com.emptrack.api.dto.*;
import com.emptrack.api.model.TblProduct;
import com.emptrack.api.model.TblWorkType;
import com.emptrack.api.model.TblWorkTypeRateHistory;
import com.emptrack.api.repository.ProductRepository;
import com.emptrack.api.repository.WorkTypeRateHistoryRepository;
import com.emptrack.api.repository.WorkTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final WorkTypeRepository workTypeRepository;
    private final WorkTypeRateHistoryRepository historyRepository;

    // ✅ Add product + work types
    @Transactional
    public void addProduct(ProductRequest req) {

        // ✅ Generate product ID
        String productId = String.format("PRD-%s-%d",
            req.getBtCode(),
            System.currentTimeMillis()
        );

        // ✅ Save product
        TblProduct product = new TblProduct();
        product.setProductId(productId);
        product.setBtCode(req.getBtCode());
        product.setCompanyCode(req.getCompanyCode());
        product.setProductName(req.getProductName());
        product.setDescription(req.getDescription());
        product.setStatus(1);
        productRepository.save(product);

        // ✅ Save work types
        AtomicInteger index = new AtomicInteger(1);
        if (req.getWorkTypes() != null) {
            req.getWorkTypes().forEach(wtReq -> {
                String workTypeId = String.format("WT-%s-%s-%03d",
                    req.getBtCode(),
                    productId,
                    index.getAndIncrement()
                );

                TblWorkType wt = new TblWorkType();
                wt.setWorkTypeId(workTypeId);
                wt.setBtCode(req.getBtCode());
                wt.setCompanyCode(req.getCompanyCode());
                wt.setProductId(productId);
                wt.setWorkTypeName(wtReq.getWorkTypeName());
                wt.setRatePerPiece(wtReq.getRatePerPiece());
                wt.setUnit(wtReq.getUnit() != null ? wtReq.getUnit() : "pieces");
                wt.setColorTag(wtReq.getColorTag() != null ? wtReq.getColorTag() : "#1565C0");
                wt.setStatus(1);
                workTypeRepository.save(wt);

                // ✅ Save initial rate as history
                saveRateHistory(
                    workTypeId,
                    productId,
                    wtReq.getWorkTypeName(),
                    req.getBtCode(),
                    req.getCompanyCode(),
                    0.0,
                    wtReq.getRatePerPiece(),
                    req.getBtCode(),
                    "Initial rate",
                    LocalDate.now()
                );
            });
        }
    }

    // ✅ Get all products with work types
    public List<ProductResponse> getProducts(String btCode, String companyCode) {
        List<TblProduct> products = productRepository
            .findByBtCodeAndCompanyCodeOrderByCreatedAtDesc(btCode, companyCode);

        return products.stream()
            .map(this::toProductResponse)
            .collect(Collectors.toList());
    }

    // ✅ Update product
    @Transactional
    public void updateProduct(String productId, ProductRequest req) {
        TblProduct product = productRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.setProductName(req.getProductName());
        product.setDescription(req.getDescription());
        productRepository.save(product);

        // ✅ Update work types if provided
        if (req.getWorkTypes() != null) {
            // ✅ Delete old work types
            workTypeRepository.deleteByProductId(productId);

            // ✅ Save new work types
            AtomicInteger index = new AtomicInteger(1);
            req.getWorkTypes().forEach(wtReq -> {
                String workTypeId = String.format("WT-%s-%s-%03d",
                    req.getBtCode(),
                    productId,
                    index.getAndIncrement()
                );

                TblWorkType wt = new TblWorkType();
                wt.setWorkTypeId(workTypeId);
                wt.setBtCode(req.getBtCode());
                wt.setCompanyCode(req.getCompanyCode());
                wt.setProductId(productId);
                wt.setWorkTypeName(wtReq.getWorkTypeName());
                wt.setRatePerPiece(wtReq.getRatePerPiece());
                wt.setUnit(wtReq.getUnit() != null ? wtReq.getUnit() : "pieces");
                wt.setColorTag(wtReq.getColorTag() != null ? wtReq.getColorTag() : "#1565C0");
                wt.setStatus(1);
                workTypeRepository.save(wt);
            });
        }
    }

    // ✅ Delete product
    @Transactional
    public void deleteProduct(String productId) {
        TblProduct product = productRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        product.setStatus(2);
        productRepository.save(product);

        // ✅ Deactivate work types too
        workTypeRepository.findByProductIdOrderByCreatedAtAsc(productId)
            .forEach(wt -> {
                wt.setStatus(2);
                workTypeRepository.save(wt);
            });
    }

    // ✅ Update rate — saves history
    @Transactional
    public void updateRate(String workTypeId, UpdateRateRequest req) {
        TblWorkType wt = workTypeRepository.findByWorkTypeId(workTypeId)
            .orElseThrow(() -> new RuntimeException("Work type not found: " + workTypeId));

        Double oldRate = wt.getRatePerPiece();

        // ✅ Save history
        saveRateHistory(
            workTypeId,
            wt.getProductId(),
            wt.getWorkTypeName(),
            wt.getBtCode(),
            wt.getCompanyCode(),
            oldRate,
            req.getNewRate(),
            req.getChangedBy(),
            req.getReason(),
            LocalDate.parse(req.getEffectiveDate())
        );

        // ✅ Update current rate
        wt.setRatePerPiece(req.getNewRate());
        workTypeRepository.save(wt);
    }

    // ✅ Get rate history
    public List<RateHistoryResponse> getRateHistory(String workTypeId) {
        return historyRepository
            .findByWorkTypeIdOrderByCreatedAtDesc(workTypeId)
            .stream()
            .map(h -> RateHistoryResponse.builder()
                .historyId(h.getHistoryId())
                .workTypeId(h.getWorkTypeId())
                .workTypeName(h.getWorkTypeName())
                .oldRate(h.getOldRate())
                .newRate(h.getNewRate())
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

    private void saveRateHistory(
        String workTypeId, String productId, String workTypeName,
        String btCode, String companyCode,
        Double oldRate, Double newRate,
        String changedBy, String reason,
        LocalDate effectiveDate
    ) {
        String historyId = String.format("RH-%s-%s-%s",
            btCode, workTypeId,
            LocalDate.now().toString().replace("-", "")
        );

        TblWorkTypeRateHistory history = new TblWorkTypeRateHistory();
        history.setHistoryId(historyId);
        history.setBtCode(btCode);
        history.setCompanyCode(companyCode);
        history.setWorkTypeId(workTypeId);
        history.setProductId(productId);
        history.setWorkTypeName(workTypeName);
        history.setOldRate(oldRate);
        history.setNewRate(newRate);
        history.setChangedBy(changedBy);
        history.setReason(reason);
        history.setEffectiveDate(effectiveDate);
        historyRepository.save(history);
    }

    private ProductResponse toProductResponse(TblProduct product) {
        List<WorkTypeResponse> workTypes = workTypeRepository
            .findByProductIdAndStatusOrderByCreatedAtAsc(product.getProductId(), 1)
            .stream()
            .map(wt -> WorkTypeResponse.builder()
                .workTypeId(wt.getWorkTypeId())
                .productId(wt.getProductId())
                .workTypeName(wt.getWorkTypeName())
                .ratePerPiece(wt.getRatePerPiece())
                .unit(wt.getUnit())
                .colorTag(wt.getColorTag())
                .status(wt.getStatus())
                .build()
            )
            .collect(Collectors.toList());

        return ProductResponse.builder()
            .productId(product.getProductId())
            .btCode(product.getBtCode())
            .companyCode(product.getCompanyCode())
            .productName(product.getProductName())
            .description(product.getDescription())
            .status(product.getStatus())
            .createdAt(product.getCreatedAt().toString())
            .workTypes(workTypes)
            .build();
    }
}