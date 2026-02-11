package com.mes.boot.workorder.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event]
 * [SOLID: SRP - 只負責攜帶工單開始事件的資料]
 *
 * 工單開始事件，在工單從 CREATED 狀態轉換到 IN_PROGRESS 狀態後觸發。
 * 可用於通知產線準備開始生產。
 */
public class WorkOrderStartedEvent extends BaseDomainEvent {

    private final String workOrderId;

    public WorkOrderStartedEvent(String workOrderId) {
        super(workOrderId);
        this.workOrderId = workOrderId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    @Override
    public String toString() {
        return "WorkOrderStartedEvent{" +
                "workOrderId='" + workOrderId + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
