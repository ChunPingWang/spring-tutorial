package com.mes.common.ddd.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * [DDD Pattern: Domain Event 抽象基礎類別]
 *
 * 提供 Domain Event 的共通實作：
 * - 自動產生唯一 eventId (UUID)
 * - 自動記錄事件發生時間
 * - 子類別只需提供 aggregateId 與事件特定資料
 */
public abstract class BaseDomainEvent implements DomainEvent {

    private final String eventId;
    private final LocalDateTime occurredOn;
    private final String aggregateId;

    protected BaseDomainEvent(String aggregateId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.aggregateId = aggregateId;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "eventId='" + eventId + '\'' +
                ", occurredOn=" + occurredOn +
                ", aggregateId='" + aggregateId + '\'' +
                '}';
    }
}
