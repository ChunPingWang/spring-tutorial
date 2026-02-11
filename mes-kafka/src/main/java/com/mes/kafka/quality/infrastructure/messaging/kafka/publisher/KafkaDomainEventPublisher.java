package com.mes.kafka.quality.infrastructure.messaging.kafka.publisher;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * [DDD Pattern: Domain Event Publisher - Adapter (出站配接器)]
 * [SOLID: DIP - 實作 DomainEventPublisher 介面，領域層不依賴 Kafka]
 * [SOLID: SRP - 只負責將領域事件發送到 Kafka]
 * [Hexagonal Architecture: Output Adapter - 訊息發送配接器]
 *
 * 使用 Spring Cloud Stream 的 {@link StreamBridge} 將領域事件發送到 Kafka。
 * 事件以 JSON 格式序列化，並在訊息標頭中附加事件元資料：
 * <ul>
 *   <li>eventType - 事件類型名稱</li>
 *   <li>aggregateId - 觸發事件的聚合根 ID</li>
 *   <li>occurredOn - 事件發生時間</li>
 * </ul>
 */
@Component
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);

    private static final String BINDING_NAME = "qualityEventsOut-out-0";

    private final StreamBridge streamBridge;

    public KafkaDomainEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing domain event to Kafka: type={}, aggregateId={}, eventId={}",
                event.getClass().getSimpleName(), event.getAggregateId(), event.getEventId());

        Message<DomainEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader("eventType", event.getClass().getSimpleName())
                .setHeader("aggregateId", event.getAggregateId())
                .setHeader("occurredOn", event.getOccurredOn().toString())
                .build();

        boolean sent = streamBridge.send(BINDING_NAME, message);

        if (sent) {
            log.info("Domain event sent successfully: {}", event.getClass().getSimpleName());
        } else {
            log.warn("Failed to send domain event: {}", event.getClass().getSimpleName());
        }
    }
}
