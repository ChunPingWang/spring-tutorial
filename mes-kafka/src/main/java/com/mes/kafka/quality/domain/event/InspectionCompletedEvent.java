package com.mes.kafka.quality.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 檢驗已完成事件]
 * [SOLID: SRP - 只負責攜帶檢驗完成的相關資訊]
 *
 * 當檢驗工單完成（PASSED 或 FAILED）時觸發此事件。
 * 包含最終狀態與不良率，供下游模組（如倉儲、出貨）做出相應決策。
 */
public class InspectionCompletedEvent extends BaseDomainEvent {

    private final String status;
    private final double defectRate;

    public InspectionCompletedEvent(String aggregateId, String status, double defectRate) {
        super(aggregateId);
        this.status = status;
        this.defectRate = defectRate;
    }

    public String getStatus() {
        return status;
    }

    public double getDefectRate() {
        return defectRate;
    }

    @Override
    public String toString() {
        return "InspectionCompletedEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", aggregateId='" + getAggregateId() + '\'' +
                ", status='" + status + '\'' +
                ", defectRate=" + defectRate +
                '}';
    }
}
