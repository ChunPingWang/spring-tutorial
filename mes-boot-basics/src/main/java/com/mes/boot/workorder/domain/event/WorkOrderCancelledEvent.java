package com.mes.boot.workorder.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event]
 * [SOLID: SRP - 只負責攜帶工單取消事件的資料]
 *
 * 工單取消事件，在工單被取消時觸發。
 * 攜帶取消原因，供下游處理器使用（例如釋放已鎖定的物料、通知排程系統）。
 */
public class WorkOrderCancelledEvent extends BaseDomainEvent {

    private final String workOrderId;
    private final String reason;

    public WorkOrderCancelledEvent(String workOrderId, String reason) {
        super(workOrderId);
        this.workOrderId = workOrderId;
        this.reason = reason;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "WorkOrderCancelledEvent{" +
                "workOrderId='" + workOrderId + '\'' +
                ", reason='" + reason + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
