package com.mes.boot.workorder.infrastructure.event;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [DDD Pattern: Domain Event Publisher - Adapter (出站適配器)]
 * [SOLID: LSP - 完全實作 DomainEventPublisher 介面的契約]
 * [SOLID: DIP - 實作領域層定義的 DomainEventPublisher 介面]
 * [Hexagonal Architecture: Output Adapter - 使用 SLF4J Logging 作為事件的出站機制]
 *
 * 基於日誌的領域事件發佈器。
 * 將領域事件以結構化日誌的方式輸出到 console，
 * 適用於 Module 1 的學習階段。
 *
 * 在後續模組中將被替換為：
 * <ul>
 *   <li>Module 2: SpringEventDomainEventPublisher（使用 Spring ApplicationEvent）</li>
 *   <li>Module 4: KafkaDomainEventPublisher（發送到 Kafka）</li>
 * </ul>
 */
@Component
public class LoggingDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingDomainEventPublisher.class);

    @Override
    public void publish(DomainEvent event) {
        log.info("[Domain Event Published] type={}, eventId={}, aggregateId={}, occurredOn={}, detail={}",
                event.getClass().getSimpleName(),
                event.getEventId(),
                event.getAggregateId(),
                event.getOccurredOn(),
                event);
    }
}
