package com.mes.mybatis.equipment.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 設備故障事件]
 *
 * 當設備發生故障時註冊此事件。
 * 下游可訂閱此事件進行通知、自動派工等操作。
 */
public class EquipmentBreakdownEvent extends BaseDomainEvent {

    private final String equipmentName;
    private final String description;

    public EquipmentBreakdownEvent(String aggregateId, String equipmentName, String description) {
        super(aggregateId);
        this.equipmentName = equipmentName;
        this.description = description;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "EquipmentBreakdownEvent{" +
                "equipmentId='" + getAggregateId() + '\'' +
                ", equipmentName='" + equipmentName + '\'' +
                ", description='" + description + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
