package com.mes.common.ddd.event;

/**
 * [DDD Pattern: Domain Event Publisher - Port (出站埠)]
 * [SOLID: DIP - 領域層定義介面，基礎設施層實作]
 * [Hexagonal Architecture: 這是一個 Output Port]
 *
 * 領域層透過此介面發佈事件，而不需要知道事件的傳送方式。
 * 可能的實作：
 * - LoggingDomainEventPublisher（記錄到 console，Module 1）
 * - SpringEventDomainEventPublisher（使用 Spring ApplicationEvent，Module 2）
 * - KafkaDomainEventPublisher（發送到 Kafka，Module 4）
 */
public interface DomainEventPublisher {

    /**
     * 發佈一個領域事件。
     */
    void publish(DomainEvent event);
}
