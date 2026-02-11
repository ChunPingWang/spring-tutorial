package com.mes.web.production.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 生產已啟動]
 * [SOLID: SRP - 只描述「生產已啟動」這一事實]
 *
 * 當生產紀錄從 PENDING 轉換為 RUNNING 時觸發。
 * 包含工單 ID 和產線 ID 供下游消費者使用。
 */
public class ProductionStartedEvent extends BaseDomainEvent {

    private final String workOrderId;
    private final String lineId;

    public ProductionStartedEvent(String aggregateId, String workOrderId, String lineId) {
        super(aggregateId);
        this.workOrderId = workOrderId;
        this.lineId = lineId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getLineId() {
        return lineId;
    }

    @Override
    public String toString() {
        return "ProductionStartedEvent{" +
                "aggregateId='" + getAggregateId() + "'" +
                ", workOrderId='" + workOrderId + "'" +
                ", lineId='" + lineId + "'" +
                ", occurredOn=" + getOccurredOn() +
                "}";
    }
}
