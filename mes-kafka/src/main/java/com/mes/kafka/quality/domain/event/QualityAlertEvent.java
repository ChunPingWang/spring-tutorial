package com.mes.kafka.quality.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 品質警報事件]
 * [SOLID: SRP - 只負責攜帶品質警報的相關資訊]
 *
 * 當檢驗完成且不良率超過預設閾值時觸發此事件。
 * 這是跨 Bounded Context 整合的關鍵事件，
 * 可觸發生產暫停、主管通知、SPC 分析等後續動作。
 */
public class QualityAlertEvent extends BaseDomainEvent {

    private final double defectRate;
    private final String productCode;

    public QualityAlertEvent(String aggregateId, double defectRate, String productCode) {
        super(aggregateId);
        this.defectRate = defectRate;
        this.productCode = productCode;
    }

    public double getDefectRate() {
        return defectRate;
    }

    public String getProductCode() {
        return productCode;
    }

    @Override
    public String toString() {
        return "QualityAlertEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", aggregateId='" + getAggregateId() + '\'' +
                ", defectRate=" + defectRate +
                ", productCode='" + productCode + '\'' +
                '}';
    }
}
