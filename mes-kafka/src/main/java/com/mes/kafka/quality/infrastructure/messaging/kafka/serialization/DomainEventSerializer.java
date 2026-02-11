package com.mes.kafka.quality.infrastructure.messaging.kafka.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mes.common.ddd.event.DomainEvent;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * [Infrastructure: Kafka Serializer - 領域事件序列化器]
 * [SOLID: SRP - 只負責將 DomainEvent 序列化為 JSON byte 陣列]
 * [SOLID: OCP - 支援所有 DomainEvent 子類別，無需修改]
 *
 * 自訂 Kafka {@link Serializer}，使用 Jackson ObjectMapper
 * 將 {@link DomainEvent} 序列化為 JSON 格式的 byte 陣列。
 *
 * <p>序列化特性：</p>
 * <ul>
 *   <li>支援 Java 8 日期時間型別（LocalDateTime 等）</li>
 *   <li>日期以 ISO-8601 格式輸出</li>
 * </ul>
 */
public class DomainEventSerializer implements Serializer<DomainEvent> {

    private static final Logger log = LoggerFactory.getLogger(DomainEventSerializer.class);

    private final ObjectMapper objectMapper;

    public DomainEventSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // 無需額外配置
    }

    @Override
    public byte[] serialize(String topic, DomainEvent data) {
        if (data == null) {
            return null;
        }
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(data);
            log.debug("Serialized DomainEvent: type={}, size={} bytes",
                    data.getClass().getSimpleName(), bytes.length);
            return bytes;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize DomainEvent: {}", data.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to serialize DomainEvent", e);
        }
    }

    @Override
    public void close() {
        // 無需清理資源
    }
}
