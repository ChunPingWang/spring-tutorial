package com.mes.mybatis.equipment.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

import java.time.LocalDate;

/**
 * [DDD Pattern: Domain Event - 維護排程事件]
 *
 * 當設備安排維護時註冊此事件。
 * 下游可訂閱此事件進行排程確認、資源準備等操作。
 */
public class MaintenanceScheduledEvent extends BaseDomainEvent {

    private final String equipmentName;
    private final String maintenanceRecordId;
    private final LocalDate scheduledDate;

    public MaintenanceScheduledEvent(String aggregateId, String equipmentName,
                                     String maintenanceRecordId, LocalDate scheduledDate) {
        super(aggregateId);
        this.equipmentName = equipmentName;
        this.maintenanceRecordId = maintenanceRecordId;
        this.scheduledDate = scheduledDate;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getMaintenanceRecordId() {
        return maintenanceRecordId;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    @Override
    public String toString() {
        return "MaintenanceScheduledEvent{" +
                "equipmentId='" + getAggregateId() + '\'' +
                ", equipmentName='" + equipmentName + '\'' +
                ", maintenanceRecordId='" + maintenanceRecordId + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
