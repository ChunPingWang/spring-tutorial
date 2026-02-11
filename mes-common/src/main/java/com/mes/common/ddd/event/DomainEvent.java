package com.mes.common.ddd.event;

import java.time.LocalDateTime;

/**
 * [DDD Pattern: Domain Event 介面]
 * [SOLID: ISP - 最小化的事件介面，只定義必要方法]
 *
 * Domain Event 代表領域中已發生的重要事實。
 * 特性：
 * 1. 不可變 (Immutable) — 事件一旦發生就不能改變
 * 2. 過去式命名 — 如 WorkOrderCreatedEvent, ProductionCompletedEvent
 * 3. 包含發生當下的必要資訊
 */
public interface DomainEvent {

    /**
     * 事件的唯一識別碼。
     */
    String getEventId();

    /**
     * 事件發生的時間。
     */
    LocalDateTime getOccurredOn();

    /**
     * 觸發此事件的聚合根 ID。
     */
    String getAggregateId();
}
