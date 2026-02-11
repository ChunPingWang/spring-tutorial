package com.mes.web.production.infrastructure.event;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * [DDD Pattern: Domain Event Publisher - Adapter (基礎設施層實作)]
 * [SOLID: DIP - 實作領域層定義的 DomainEventPublisher 介面]
 * [SOLID: LSP - 可替換為 Kafka、RabbitMQ 等其他實作]
 * [Hexagonal Architecture: Output Adapter]
 *
 * 使用 Spring ApplicationEventPublisher 發佈領域事件。
 * 事件會在同一個 JVM 內以同步方式傳遞給所有 @EventListener。
 *
 * 在 Module 4 (mes-kafka) 中，將替換為 Kafka 實作以支援跨服務事件傳遞。
 */
@Component
public class SpringEventDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringEventDomainEventPublisher.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info("發佈領域事件: {}", event);
        applicationEventPublisher.publishEvent(event);
    }
}
