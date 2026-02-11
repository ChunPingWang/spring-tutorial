package com.mes.kafka.quality.domain.model;

import com.mes.common.ddd.annotation.AggregateRoot;
import com.mes.common.ddd.model.BaseAggregateRoot;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.kafka.quality.domain.event.DefectDetectedEvent;
import com.mes.kafka.quality.domain.event.InspectionCompletedEvent;
import com.mes.kafka.quality.domain.event.InspectionOrderCreatedEvent;
import com.mes.kafka.quality.domain.event.QualityAlertEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * [DDD Pattern: Aggregate Root - 檢驗工單]
 * [SOLID: SRP - 負責維護檢驗工單聚合的一致性與業務規則]
 * [SOLID: OCP - 可擴展新的檢驗行為而不修改現有邏輯]
 *
 * 檢驗工單聚合根，是品質檢驗 Bounded Context 的核心領域物件。
 * 管理檢驗的完整生命週期：建立 → 開始 → 記錄結果 → 完成。
 *
 * <p>聚合邊界內包含：</p>
 * <ul>
 *   <li>{@link InspectionResult} - 檢驗結果（內部 Entity）</li>
 *   <li>{@link QualityStandard} - 品質標準（Value Object）</li>
 *   <li>{@link MeasuredValue} - 量測值（Value Object）</li>
 *   <li>{@link DefectDetail} - 缺陷詳情（Value Object）</li>
 * </ul>
 *
 * <p>領域事件：</p>
 * <ul>
 *   <li>{@link InspectionOrderCreatedEvent} - 檢驗開始時觸發</li>
 *   <li>{@link DefectDetectedEvent} - 檢測到缺陷時觸發</li>
 *   <li>{@link InspectionCompletedEvent} - 檢驗完成時觸發</li>
 *   <li>{@link QualityAlertEvent} - 不良率超過閾值時觸發</li>
 * </ul>
 */
@AggregateRoot
public class InspectionOrder extends BaseAggregateRoot<InspectionOrderId> {

    private final String workOrderId;
    private final String productCode;
    private final InspectionType type;
    private InspectionStatus status;
    private final List<InspectionResult> results;
    private final double defectRateThreshold;

    /**
     * 建立檢驗工單，初始狀態為 PENDING。
     *
     * @param id          檢驗工單 ID
     * @param workOrderId 關聯的工單 ID
     * @param productCode 產品代碼
     * @param type        檢驗類型
     */
    public InspectionOrder(InspectionOrderId id, String workOrderId, String productCode, InspectionType type) {
        this(id, workOrderId, productCode, type, 0.10);
    }

    /**
     * 建立檢驗工單，可自訂不良率閾值。
     *
     * @param id                   檢驗工單 ID
     * @param workOrderId          關聯的工單 ID
     * @param productCode          產品代碼
     * @param type                 檢驗類型
     * @param defectRateThreshold  不良率閾值（超過此值將觸發品質警報）
     */
    public InspectionOrder(InspectionOrderId id, String workOrderId, String productCode,
                           InspectionType type, double defectRateThreshold) {
        super(id);
        this.workOrderId = Objects.requireNonNull(workOrderId, "Work order ID must not be null");
        this.productCode = Objects.requireNonNull(productCode, "Product code must not be null");
        this.type = Objects.requireNonNull(type, "Inspection type must not be null");
        this.status = InspectionStatus.PENDING;
        this.results = new ArrayList<>();
        this.defectRateThreshold = defectRateThreshold;
    }

    /**
     * 開始檢驗，狀態從 PENDING 轉為 IN_PROGRESS。
     * 觸發 {@link InspectionOrderCreatedEvent}。
     *
     * @throws DomainException 若當前狀態不是 PENDING
     */
    public void startInspection() {
        if (status != InspectionStatus.PENDING) {
            throw new DomainException("Cannot start inspection: current status is " + status);
        }
        this.status = InspectionStatus.IN_PROGRESS;
        touch();
        registerEvent(new InspectionOrderCreatedEvent(
                getId().getValue(), workOrderId, productCode, type.name()));
    }

    /**
     * 記錄檢驗結果。
     * 自動根據標準判定合格與否，不合格時觸發 {@link DefectDetectedEvent}。
     *
     * @param standard 品質標準
     * @param value    量測值
     * @throws DomainException 若當前狀態不是 IN_PROGRESS
     */
    public void recordResult(QualityStandard standard, MeasuredValue value) {
        validateInProgress();
        InspectionResultId resultId = new InspectionResultId(UUID.randomUUID().toString());
        InspectionResult result = new InspectionResult(resultId, standard, value);
        results.add(result);
        touch();

        if (!result.isPassed()) {
            registerEvent(new DefectDetectedEvent(
                    getId().getValue(), null, standard.getStandardCode()));
        }
    }

    /**
     * 記錄帶有缺陷詳情的檢驗結果。
     * 當量測值超標時可附加缺陷詳情，觸發 {@link DefectDetectedEvent}。
     *
     * @param standard 品質標準
     * @param value    量測值
     * @param defect   缺陷詳情
     * @throws DomainException 若當前狀態不是 IN_PROGRESS
     */
    public void recordResultWithDefect(QualityStandard standard, MeasuredValue value, DefectDetail defect) {
        validateInProgress();
        InspectionResultId resultId = new InspectionResultId(UUID.randomUUID().toString());
        InspectionResult result = new InspectionResult(resultId, standard, value, defect);
        results.add(result);
        touch();

        if (!result.isPassed()) {
            registerEvent(new DefectDetectedEvent(
                    getId().getValue(),
                    defect != null ? defect.getDefectCode() : null,
                    standard.getStandardCode()));
        }
    }

    /**
     * 完成檢驗。
     * 根據檢驗結果判定最終狀態（PASSED / FAILED），
     * 若不良率超過閾值則觸發 {@link QualityAlertEvent}，
     * 最終觸發 {@link InspectionCompletedEvent}。
     *
     * @throws DomainException               若當前狀態不是 IN_PROGRESS
     * @throws BusinessRuleViolationException 若沒有任何檢驗結果
     */
    public void complete() {
        validateInProgress();
        if (results.isEmpty()) {
            throw new BusinessRuleViolationException("Cannot complete inspection without any results");
        }

        double defectRate = getDefectRate();

        if (defectRate > 0) {
            this.status = InspectionStatus.FAILED;
        } else {
            this.status = InspectionStatus.PASSED;
        }

        touch();

        // 不良率超過閾值時觸發品質警報
        if (defectRate > defectRateThreshold) {
            registerEvent(new QualityAlertEvent(
                    getId().getValue(), defectRate, productCode));
        }

        registerEvent(new InspectionCompletedEvent(
                getId().getValue(), status.name(), defectRate));
    }

    /**
     * 暫停檢驗。
     * 狀態從 IN_PROGRESS 轉為 ON_HOLD。
     *
     * @param reason 暫停原因
     * @throws DomainException 若當前狀態不是 IN_PROGRESS
     */
    public void putOnHold(String reason) {
        validateInProgress();
        this.status = InspectionStatus.ON_HOLD;
        touch();
    }

    /**
     * 計算不良率。
     *
     * @return 不合格結果數量 / 總結果數量，若無結果則返回 0.0
     */
    public double getDefectRate() {
        if (results.isEmpty()) {
            return 0.0;
        }
        long failedCount = 0;
        for (InspectionResult result : results) {
            if (!result.isPassed()) {
                failedCount++;
            }
        }
        return (double) failedCount / results.size();
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public InspectionType getType() {
        return type;
    }

    public InspectionStatus getStatus() {
        return status;
    }

    public List<InspectionResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public double getDefectRateThreshold() {
        return defectRateThreshold;
    }

    private void validateInProgress() {
        if (status != InspectionStatus.IN_PROGRESS) {
            throw new DomainException("Inspection must be IN_PROGRESS, current status is " + status);
        }
    }

    @Override
    public String toString() {
        return "InspectionOrder{" +
                "id=" + getId() +
                ", workOrderId='" + workOrderId + '\'' +
                ", productCode='" + productCode + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", resultsCount=" + results.size() +
                '}';
    }
}
