package com.mes.boot.workorder.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event]
 * [SOLID: SRP - 只負責攜帶工單完成事件的資料]
 *
 * 工單完成事件，在工單從 IN_PROGRESS 狀態轉換到 COMPLETED 狀態後觸發。
 * 攜帶完成量與不良量資訊，供下游處理器使用（例如更新庫存、品質統計）。
 */
public class WorkOrderCompletedEvent extends BaseDomainEvent {

    private final String workOrderId;
    private final int planned;
    private final int completed;
    private final int defective;

    public WorkOrderCompletedEvent(String workOrderId, int planned, int completed, int defective) {
        super(workOrderId);
        this.workOrderId = workOrderId;
        this.planned = planned;
        this.completed = completed;
        this.defective = defective;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public int getPlanned() {
        return planned;
    }

    public int getCompleted() {
        return completed;
    }

    public int getDefective() {
        return defective;
    }

    @Override
    public String toString() {
        return "WorkOrderCompletedEvent{" +
                "workOrderId='" + workOrderId + '\'' +
                ", planned=" + planned +
                ", completed=" + completed +
                ", defective=" + defective +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
