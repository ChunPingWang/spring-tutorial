package com.mes.kafka.quality.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 缺陷偵測事件]
 * [SOLID: SRP - 只負責攜帶缺陷偵測的相關資訊]
 *
 * 當檢驗結果判定為不合格時觸發此事件。
 * 每發現一筆缺陷就觸發一次，可用於即時缺陷追蹤與統計。
 */
public class DefectDetectedEvent extends BaseDomainEvent {

    private final String defectCode;
    private final String standardCode;

    public DefectDetectedEvent(String aggregateId, String defectCode, String standardCode) {
        super(aggregateId);
        this.defectCode = defectCode;
        this.standardCode = standardCode;
    }

    public String getDefectCode() {
        return defectCode;
    }

    public String getStandardCode() {
        return standardCode;
    }

    @Override
    public String toString() {
        return "DefectDetectedEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", aggregateId='" + getAggregateId() + '\'' +
                ", defectCode='" + defectCode + '\'' +
                ", standardCode='" + standardCode + '\'' +
                '}';
    }
}
