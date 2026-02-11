package com.mes.web.production.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 不良品已記錄]
 * [SOLID: SRP - 只描述「有不良品被記錄」這一事實]
 *
 * 當記錄產出時發現不良品數量大於 0 時觸發。
 * 供品質管理系統或告警系統消費。
 */
public class DefectRecordedEvent extends BaseDomainEvent {

    private final int defectCount;

    public DefectRecordedEvent(String aggregateId, int defectCount) {
        super(aggregateId);
        this.defectCount = defectCount;
    }

    public int getDefectCount() {
        return defectCount;
    }

    @Override
    public String toString() {
        return "DefectRecordedEvent{" +
                "aggregateId='" + getAggregateId() + "'" +
                ", defectCount=" + defectCount +
                ", occurredOn=" + getOccurredOn() +
                "}";
    }
}
