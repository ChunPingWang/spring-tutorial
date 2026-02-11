package com.mes.kafka.quality.infrastructure.messaging.kafka.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mes.common.ddd.event.DomainEvent;
import com.mes.kafka.quality.domain.event.DefectDetectedEvent;
import com.mes.kafka.quality.domain.event.InspectionCompletedEvent;
import com.mes.kafka.quality.domain.event.InspectionOrderCreatedEvent;
import com.mes.kafka.quality.domain.event.QualityAlertEvent;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * [Infrastructure: Kafka Deserializer - 領域事件反序列化器]
 * [SOLID: SRP - 只負責將 JSON byte 陣列反序列化為 DomainEvent]
 * [SOLID: OCP - 透過 eventTypeMapping 註冊新事件類型，無需修改核心邏輯]
 *
 * 自訂 Kafka {@link Deserializer}，根據訊息標頭中的 eventType
 * 將 JSON byte 陣列反序列化為對應的 {@link DomainEvent} 子類別。
 *
 * <p>反序列化流程：</p>
 * <ol>
 *   <li>從 Kafka 訊息標頭讀取 eventType</li>
 *   <li>根據 eventType 查找對應的 Java 類別</li>
 *   <li>使用 Jackson ObjectMapper 進行反序列化</li>
 * </ol>
 */
public class DomainEventDeserializer implements Deserializer<DomainEvent> {

    private static final Logger log = LoggerFactory.getLogger(DomainEventDeserializer.class);

    private final ObjectMapper objectMapper;
    private final Map<String, Class<? extends DomainEvent>> eventTypeMapping;

    public DomainEventDeserializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.eventTypeMapping = new HashMap<>();
        this.eventTypeMapping.put("InspectionOrderCreatedEvent", InspectionOrderCreatedEvent.class);
        this.eventTypeMapping.put("InspectionCompletedEvent", InspectionCompletedEvent.class);
        this.eventTypeMapping.put("QualityAlertEvent", QualityAlertEvent.class);
        this.eventTypeMapping.put("DefectDetectedEvent", DefectDetectedEvent.class);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // 無需額外配置
    }

    @Override
    public DomainEvent deserialize(String topic, byte[] data) {
        // 不含 headers 的版本，嘗試以預設類型反序列化
        return deserialize(topic, null, data);
    }

    @Override
    public DomainEvent deserialize(String topic, Headers headers, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            String eventType = extractEventType(headers);
            Class<? extends DomainEvent> eventClass = eventTypeMapping.get(eventType);

            if (eventClass != null) {
                DomainEvent event = objectMapper.readValue(data, eventClass);
                log.debug("Deserialized DomainEvent: type={}", eventType);
                return event;
            } else {
                // 未知事件類型，嘗試以通用方式反序列化
                log.warn("Unknown event type: {}, attempting generic deserialization", eventType);
                return objectMapper.readValue(data, DomainEvent.class);
            }
        } catch (Exception e) {
            log.error("Failed to deserialize DomainEvent from topic: {}", topic, e);
            throw new RuntimeException("Failed to deserialize DomainEvent", e);
        }
    }

    /**
     * 從 Kafka Headers 中提取 eventType。
     */
    private String extractEventType(Headers headers) {
        if (headers == null) {
            return null;
        }
        Header header = headers.lastHeader("eventType");
        if (header != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }

    @Override
    public void close() {
        // 無需清理資源
    }
}
