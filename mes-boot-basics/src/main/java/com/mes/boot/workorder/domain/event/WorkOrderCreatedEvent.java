package com.mes.boot.workorder.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event]
 * [SOLID: SRP - 只負責攜帶工單建立事件的資料]
 *
 * 工單建立事件，在工單透過工廠方法成功建立後觸發。
 * 攜帶工單 ID 與產品代碼，供下游事件處理器使用（例如通知排程系統）。
 */
public class WorkOrderCreatedEvent extends BaseDomainEvent {

    private final String workOrderId;
    private final String productCode;

    public WorkOrderCreatedEvent(String workOrderId, String productCode) {
        super(workOrderId);
        this.workOrderId = workOrderId;
        this.productCode = productCode;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    @Override
    public String toString() {
        return "WorkOrderCreatedEvent{" +
                "workOrderId='" + workOrderId + '\'' +
                ", productCode='" + productCode + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
