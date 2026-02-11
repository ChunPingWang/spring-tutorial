package com.mes.kafka.quality.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 檢驗工單已建立事件]
 * [SOLID: SRP - 只負責攜帶檢驗工單建立的相關資訊]
 *
 * 當檢驗工單開始檢驗時觸發此事件。
 * 其他 Bounded Context（如排程模組）可訂閱此事件以進行後續處理。
 */
public class InspectionOrderCreatedEvent extends BaseDomainEvent {

    private final String workOrderId;
    private final String productCode;
    private final String inspectionType;

    public InspectionOrderCreatedEvent(String aggregateId, String workOrderId,
                                       String productCode, String inspectionType) {
        super(aggregateId);
        this.workOrderId = workOrderId;
        this.productCode = productCode;
        this.inspectionType = inspectionType;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    @Override
    public String toString() {
        return "InspectionOrderCreatedEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", aggregateId='" + getAggregateId() + '\'' +
                ", workOrderId='" + workOrderId + '\'' +
                ", productCode='" + productCode + '\'' +
                ", inspectionType='" + inspectionType + '\'' +
                '}';
    }
}
