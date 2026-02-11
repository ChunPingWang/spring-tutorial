package com.mes.mybatis.equipment.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 維護完成事件]
 *
 * 當設備維護完成時註冊此事件。
 * 下游可訂閱此事件更新設備可用性、記錄維護歷史等。
 */
public class MaintenanceCompletedEvent extends BaseDomainEvent {

    private final String equipmentName;
    private final String maintenanceRecordId;
    private final String technicianName;

    public MaintenanceCompletedEvent(String aggregateId, String equipmentName,
                                     String maintenanceRecordId, String technicianName) {
        super(aggregateId);
        this.equipmentName = equipmentName;
        this.maintenanceRecordId = maintenanceRecordId;
        this.technicianName = technicianName;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getMaintenanceRecordId() {
        return maintenanceRecordId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    @Override
    public String toString() {
        return "MaintenanceCompletedEvent{" +
                "equipmentId='" + getAggregateId() + '\'' +
                ", equipmentName='" + equipmentName + '\'' +
                ", maintenanceRecordId='" + maintenanceRecordId + '\'' +
                ", technicianName='" + technicianName + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
