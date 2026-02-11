package com.mes.web.production.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;
import com.mes.web.production.domain.model.OutputQuantity;

/**
 * [DDD Pattern: Domain Event - 生產已完成]
 * [SOLID: SRP - 只描述「生產已完成」這一事實及最終產出]
 *
 * 當生產紀錄從 RUNNING 轉換為 FINISHED 時觸發。
 * 包含工單 ID、產品代碼和最終產出數量，
 * 供下游消費者（如品質系統、庫存系統）使用。
 */
public class ProductionCompletedEvent extends BaseDomainEvent {

    private final String workOrderId;
    private final String productCode;
    private final OutputQuantity output;

    public ProductionCompletedEvent(String aggregateId,
                                    String workOrderId,
                                    String productCode,
                                    OutputQuantity output) {
        super(aggregateId);
        this.workOrderId = workOrderId;
        this.productCode = productCode;
        this.output = output;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public OutputQuantity getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "ProductionCompletedEvent{" +
                "aggregateId='" + getAggregateId() + "'" +
                ", workOrderId='" + workOrderId + "'" +
                ", productCode='" + productCode + "'" +
                ", output=" + output +
                ", occurredOn=" + getOccurredOn() +
                "}";
    }
}
