package com.mes.web.production.domain.model;

import com.mes.common.ddd.annotation.AggregateRoot;
import com.mes.common.ddd.model.BaseAggregateRoot;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.web.production.domain.event.DefectRecordedEvent;
import com.mes.web.production.domain.event.ProductionCompletedEvent;
import com.mes.web.production.domain.event.ProductionStartedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [DDD Pattern: Aggregate Root - 生產紀錄]
 * [SOLID: SRP - 負責維護生產紀錄聚合的一致性]
 * [SOLID: OCP - 透過 Domain Event 擴展行為而不修改核心邏輯]
 *
 * ProductionRecord 是生產追蹤的核心聚合根，封裝了一筆生產紀錄的完整生命週期。
 *
 * 聚合邊界內包含：
 * - ProductionLine (Entity) — 所屬產線
 * - OutputQuantity (Value Object) — 產出數量
 * - OperatorInfo (Value Object) — 操作員資訊
 * - ProcessStep (Value Object) — 製程步驟列表
 *
 * 狀態轉換規則：
 * <pre>
 *   PENDING --start()--> RUNNING --pause()--> PAUSED
 *                          |                    |
 *                          |                    +--resume()--> RUNNING
 *                          |
 *                          +--finish()--> FINISHED
 * </pre>
 *
 * 每次重要的狀態變更都會註冊對應的 Domain Event。
 */
@AggregateRoot
public class ProductionRecord extends BaseAggregateRoot<ProductionRecordId> {

    private final ProductionLine productionLine;
    private final String workOrderId;
    private final String productCode;
    private ProductionStatus status;
    private OutputQuantity output;
    private final OperatorInfo operator;
    private final List<ProcessStep> steps;

    /**
     * 建構新的生產紀錄。
     * 初始狀態為 PENDING，產出為零。
     *
     * @param id             生產紀錄 ID
     * @param productionLine 所屬產線
     * @param workOrderId    工單 ID
     * @param productCode    產品代碼
     * @param operator       操作員資訊
     */
    public ProductionRecord(ProductionRecordId id,
                            ProductionLine productionLine,
                            String workOrderId,
                            String productCode,
                            OperatorInfo operator) {
        super(id);
        if (productionLine == null) {
            throw new DomainException("產線不可為空");
        }
        if (workOrderId == null || workOrderId.trim().isEmpty()) {
            throw new DomainException("工單 ID 不可為空");
        }
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new DomainException("產品代碼不可為空");
        }
        if (operator == null) {
            throw new DomainException("操作員資訊不可為空");
        }
        this.productionLine = productionLine;
        this.workOrderId = workOrderId;
        this.productCode = productCode;
        this.operator = operator;
        this.status = ProductionStatus.PENDING;
        this.output = OutputQuantity.zero();
        this.steps = new ArrayList<ProcessStep>();
    }

    /**
     * 啟動生產。
     * 只有 PENDING 狀態可以啟動，啟動後狀態變為 RUNNING。
     * 註冊 ProductionStartedEvent。
     *
     * @throws BusinessRuleViolationException 如果目前狀態不是 PENDING
     */
    public void start() {
        if (status != ProductionStatus.PENDING) {
            throw new BusinessRuleViolationException(
                    "只有待開始狀態可以啟動生產，目前狀態: " + status.getDescription());
        }
        this.status = ProductionStatus.RUNNING;
        touch();
        registerEvent(new ProductionStartedEvent(
                getId().getValue(),
                workOrderId,
                productionLine.getLineId().getValue()));
    }

    /**
     * 暫停生產。
     * 只有 RUNNING 狀態可以暫停。
     *
     * @throws BusinessRuleViolationException 如果目前狀態不是 RUNNING
     */
    public void pause() {
        if (status != ProductionStatus.RUNNING) {
            throw new BusinessRuleViolationException(
                    "只有進行中狀態可以暫停生產，目前狀態: " + status.getDescription());
        }
        this.status = ProductionStatus.PAUSED;
        touch();
    }

    /**
     * 恢復生產。
     * 只有 PAUSED 狀態可以恢復。
     *
     * @throws BusinessRuleViolationException 如果目前狀態不是 PAUSED
     */
    public void resume() {
        if (status != ProductionStatus.PAUSED) {
            throw new BusinessRuleViolationException(
                    "只有已暫停狀態可以恢復生產，目前狀態: " + status.getDescription());
        }
        this.status = ProductionStatus.RUNNING;
        touch();
    }

    /**
     * 記錄產出數量。
     * 更新產出數據，若有不良品則註冊 DefectRecordedEvent。
     *
     * @param newOutput 新的產出數量
     * @throws DomainException 如果 newOutput 為 null
     */
    public void recordOutput(OutputQuantity newOutput) {
        if (newOutput == null) {
            throw new DomainException("產出數量不可為空");
        }
        this.output = newOutput;
        touch();

        if (newOutput.getDefective() > 0) {
            registerEvent(new DefectRecordedEvent(
                    getId().getValue(),
                    newOutput.getDefective()));
        }
    }

    /**
     * 完成生產。
     * 只有 RUNNING 狀態可以完成，完成後狀態變為 FINISHED。
     * 註冊 ProductionCompletedEvent。
     *
     * @throws BusinessRuleViolationException 如果目前狀態不是 RUNNING
     */
    public void finish() {
        if (status != ProductionStatus.RUNNING) {
            throw new BusinessRuleViolationException(
                    "只有進行中狀態可以完成生產，目前狀態: " + status.getDescription());
        }
        this.status = ProductionStatus.FINISHED;
        touch();
        registerEvent(new ProductionCompletedEvent(
                getId().getValue(),
                workOrderId,
                productCode,
                output));
    }

    /**
     * 新增製程步驟。
     *
     * @param step 製程步驟
     */
    public void addStep(ProcessStep step) {
        if (step == null) {
            throw new DomainException("製程步驟不可為空");
        }
        this.steps.add(step);
        touch();
    }

    // ========== Getters ==========

    public ProductionLine getProductionLine() {
        return productionLine;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public ProductionStatus getStatus() {
        return status;
    }

    public OutputQuantity getOutput() {
        return output;
    }

    public OperatorInfo getOperator() {
        return operator;
    }

    /**
     * 回傳製程步驟的唯讀列表。
     *
     * @return 不可變的步驟列表
     */
    public List<ProcessStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    @Override
    public String toString() {
        return "ProductionRecord{id=" + getId() +
                ", workOrderId='" + workOrderId + "'" +
                ", productCode='" + productCode + "'" +
                ", status=" + status +
                ", output=" + output +
                "}";
    }
}
