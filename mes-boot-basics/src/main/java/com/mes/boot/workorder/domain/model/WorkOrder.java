package com.mes.boot.workorder.domain.model;

import com.mes.boot.workorder.domain.event.WorkOrderCancelledEvent;
import com.mes.boot.workorder.domain.event.WorkOrderCompletedEvent;
import com.mes.boot.workorder.domain.event.WorkOrderCreatedEvent;
import com.mes.boot.workorder.domain.event.WorkOrderStartedEvent;
import com.mes.common.ddd.annotation.AggregateRoot;
import com.mes.common.ddd.model.BaseAggregateRoot;
import com.mes.common.exception.DomainException;

import java.util.Objects;

/**
 * [DDD Pattern: Aggregate Root]
 * [SOLID: SRP - 只負責維護工單聚合的一致性與狀態轉換]
 * [SOLID: OCP - 可透過新增事件與方法擴展行為，不修改既有邏輯]
 *
 * 工單聚合根，是 MES 系統中生產管理的核心領域物件。
 * 封裝了工單的完整生命週期邏輯：
 *
 * <pre>
 *   建立 (CREATED)
 *     ├── 開始 (start) → IN_PROGRESS
 *     │     ├── 完成 (complete) → COMPLETED
 *     │     └── 取消 (cancel)   → CANCELLED
 *     └── 取消 (cancel) → CANCELLED
 * </pre>
 *
 * 所有狀態轉換都會進行驗證，並在成功後註冊對應的領域事件。
 * 外部不可直接修改工單狀態，只能透過聚合根提供的行為方法。
 */
@AggregateRoot
public class WorkOrder extends BaseAggregateRoot<WorkOrderId> {

    private WorkOrderStatus status;
    private ProductInfo productInfo;
    private Quantity quantity;
    private Priority priority;
    private DateRange dateRange;

    /**
     * 建構子，初始化工單並註冊建立事件。
     * 建議透過 {@link com.mes.boot.workorder.domain.factory.WorkOrderFactory} 建立，
     * 以確保所有業務規則被正確驗證。
     *
     * @param id          工單識別碼
     * @param productInfo 產品資訊
     * @param quantity    數量
     * @param priority    優先順序
     * @param dateRange   計畫日期範圍
     */
    public WorkOrder(WorkOrderId id, ProductInfo productInfo, Quantity quantity,
                     Priority priority, DateRange dateRange) {
        super(id);
        this.productInfo = Objects.requireNonNull(productInfo, "Product info must not be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity must not be null");
        this.priority = Objects.requireNonNull(priority, "Priority must not be null");
        this.dateRange = Objects.requireNonNull(dateRange, "Date range must not be null");
        this.status = WorkOrderStatus.CREATED;
        registerEvent(new WorkOrderCreatedEvent(id.getValue(), productInfo.getProductCode()));
    }

    /**
     * 開始生產 — 將工單從 CREATED 狀態轉換為 IN_PROGRESS。
     *
     * @throws DomainException 若工單不在 CREATED 狀態
     */
    public void start() {
        if (status != WorkOrderStatus.CREATED) {
            throw new DomainException(
                    "Cannot start work order. Current status: " + status + ", expected: CREATED");
        }
        this.status = WorkOrderStatus.IN_PROGRESS;
        touch();
        registerEvent(new WorkOrderStartedEvent(getId().getValue()));
    }

    /**
     * 完成生產 — 將工單從 IN_PROGRESS 狀態轉換為 COMPLETED。
     *
     * @param actualQuantity 實際完成的數量（含計畫量、完成量、不良量）
     * @throws DomainException 若工單不在 IN_PROGRESS 狀態，或完成量超過計畫量
     */
    public void complete(Quantity actualQuantity) {
        Objects.requireNonNull(actualQuantity, "Actual quantity must not be null");

        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new DomainException(
                    "Cannot complete work order. Current status: " + status + ", expected: IN_PROGRESS");
        }
        if (actualQuantity.getCompleted() > this.quantity.getPlanned()) {
            throw new DomainException(
                    "Completed quantity (" + actualQuantity.getCompleted() +
                            ") cannot exceed planned quantity (" + this.quantity.getPlanned() + ")");
        }

        this.quantity = actualQuantity;
        this.status = WorkOrderStatus.COMPLETED;
        touch();
        registerEvent(new WorkOrderCompletedEvent(
                getId().getValue(),
                actualQuantity.getPlanned(),
                actualQuantity.getCompleted(),
                actualQuantity.getDefective()));
    }

    /**
     * 取消工單 — 可從 CREATED 或 IN_PROGRESS 狀態轉換為 CANCELLED。
     *
     * @param reason 取消原因
     * @throws DomainException 若工單已完成（COMPLETED 為終態，不可取消）
     */
    public void cancel(String reason) {
        if (status == WorkOrderStatus.COMPLETED) {
            throw new DomainException("Cannot cancel a completed work order");
        }
        if (status == WorkOrderStatus.CANCELLED) {
            throw new DomainException("Work order is already cancelled");
        }

        this.status = WorkOrderStatus.CANCELLED;
        touch();
        registerEvent(new WorkOrderCancelledEvent(getId().getValue(), reason));
    }

    // ─── Getters ───────────────────────────────────────────────

    public WorkOrderStatus getStatus() {
        return status;
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Priority getPriority() {
        return priority;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    @Override
    public String toString() {
        return "WorkOrder{" +
                "id=" + getId() +
                ", status=" + status +
                ", product=" + productInfo.getProductCode() +
                ", priority=" + priority +
                '}';
    }
}
