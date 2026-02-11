package com.mes.kafka.quality.infrastructure.messaging.kafka.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.kafka.quality.domain.event.InspectionCompletedEvent;
import com.mes.kafka.quality.domain.event.InspectionOrderCreatedEvent;
import com.mes.kafka.quality.domain.event.QualityAlertEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DomainEventSerializer 單元測試。
 * 驗證領域事件的 JSON 序列化邏輯。
 */
@DisplayName("DomainEventSerializer 序列化器測試")
class DomainEventSerializerTest {

    private DomainEventSerializer serializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        serializer = new DomainEventSerializer();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("應正確序列化 InspectionOrderCreatedEvent 為 JSON")
    void shouldSerializeInspectionOrderCreatedEvent() throws Exception {
        InspectionOrderCreatedEvent event = new InspectionOrderCreatedEvent(
                "INS-001", "WO-001", "PRODUCT-A", "FINAL");

        byte[] bytes = serializer.serialize("test-topic", event);

        assertThat(bytes).isNotNull();
        assertThat(bytes.length).isGreaterThan(0);

        JsonNode json = objectMapper.readTree(bytes);
        assertThat(json.get("aggregateId").asText()).isEqualTo("INS-001");
        assertThat(json.get("workOrderId").asText()).isEqualTo("WO-001");
        assertThat(json.get("productCode").asText()).isEqualTo("PRODUCT-A");
        assertThat(json.get("inspectionType").asText()).isEqualTo("FINAL");
        assertThat(json.get("eventId").asText()).isNotEmpty();
    }

    @Test
    @DisplayName("應正確序列化 InspectionCompletedEvent 為 JSON")
    void shouldSerializeInspectionCompletedEvent() throws Exception {
        InspectionCompletedEvent event = new InspectionCompletedEvent(
                "INS-001", "PASSED", 0.05);

        byte[] bytes = serializer.serialize("test-topic", event);

        assertThat(bytes).isNotNull();
        JsonNode json = objectMapper.readTree(bytes);
        assertThat(json.get("status").asText()).isEqualTo("PASSED");
        assertThat(json.get("defectRate").asDouble()).isEqualTo(0.05);
    }

    @Test
    @DisplayName("應正確序列化 QualityAlertEvent 為 JSON")
    void shouldSerializeQualityAlertEvent() throws Exception {
        QualityAlertEvent event = new QualityAlertEvent(
                "INS-002", 0.25, "PRODUCT-B");

        byte[] bytes = serializer.serialize("test-topic", event);

        assertThat(bytes).isNotNull();
        JsonNode json = objectMapper.readTree(bytes);
        assertThat(json.get("defectRate").asDouble()).isEqualTo(0.25);
        assertThat(json.get("productCode").asText()).isEqualTo("PRODUCT-B");
    }

    @Test
    @DisplayName("序列化 null 應回傳 null")
    void shouldReturnNullForNullEvent() {
        byte[] bytes = serializer.serialize("test-topic", null);
        assertThat(bytes).isNull();
    }

    @Test
    @DisplayName("序列化結果應包含 occurredOn 欄位（ISO-8601 格式）")
    void shouldContainOccurredOnInIsoFormat() throws Exception {
        InspectionOrderCreatedEvent event = new InspectionOrderCreatedEvent(
                "INS-001", "WO-001", "PRODUCT-A", "FINAL");

        byte[] bytes = serializer.serialize("test-topic", event);
        JsonNode json = objectMapper.readTree(bytes);

        // occurredOn 應為 ISO-8601 格式字串（非 timestamp 數值）
        assertThat(json.get("occurredOn").isTextual()).isTrue();
        assertThat(json.get("occurredOn").asText()).contains("T"); // ISO-8601 包含 T 分隔符
    }
}
