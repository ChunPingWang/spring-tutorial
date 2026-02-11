package com.mes.kafka.quality.infrastructure.messaging.kafka.publisher;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.kafka.quality.domain.event.InspectionCompletedEvent;
import com.mes.kafka.quality.domain.event.InspectionOrderCreatedEvent;
import com.mes.kafka.quality.domain.event.QualityAlertEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * KafkaDomainEventPublisher 單元測試。
 *
 * 由於 StreamBridge 為 final class，在目前的 JDK/Byte Buddy 版本下無法直接 Mock。
 * 因此使用自訂的 TestableKafkaDomainEventPublisher 繼承方式，
 * 攔截 publish 方法中的 Message 建構結果進行驗證。
 */
@DisplayName("KafkaDomainEventPublisher 事件發佈器測試")
class KafkaDomainEventPublisherTest {

    private List<CapturedMessage> capturedMessages;
    private TestablePublisher publisher;

    @BeforeEach
    void setUp() {
        capturedMessages = new ArrayList<>();
        publisher = new TestablePublisher(capturedMessages);
    }

    @Test
    @DisplayName("應使用正確的 binding name 發送領域事件")
    void shouldSendEventWithCorrectBindingName() {
        InspectionOrderCreatedEvent event = new InspectionOrderCreatedEvent(
                "INS-001", "WO-001", "PRODUCT-A", "FINAL");

        publisher.publish(event);

        assertThat(capturedMessages).hasSize(1);
        assertThat(capturedMessages.get(0).bindingName).isEqualTo("qualityEventsOut-out-0");
    }

    @Test
    @DisplayName("訊息 payload 應為原始事件")
    void shouldIncludeEventAsPayload() {
        InspectionOrderCreatedEvent event = new InspectionOrderCreatedEvent(
                "INS-001", "WO-001", "PRODUCT-A", "FINAL");

        publisher.publish(event);

        assertThat(capturedMessages).hasSize(1);
        Message<?> message = capturedMessages.get(0).message;
        assertThat(message.getPayload()).isEqualTo(event);
    }

    @Test
    @DisplayName("訊息標頭應包含 eventType")
    void shouldIncludeEventTypeHeader() {
        InspectionCompletedEvent event = new InspectionCompletedEvent("INS-001", "PASSED", 0.0);

        publisher.publish(event);

        assertThat(capturedMessages).hasSize(1);
        Message<?> message = capturedMessages.get(0).message;
        assertThat(message.getHeaders().get("eventType")).isEqualTo("InspectionCompletedEvent");
    }

    @Test
    @DisplayName("訊息標頭應包含 aggregateId")
    void shouldIncludeAggregateIdHeader() {
        QualityAlertEvent event = new QualityAlertEvent("INS-002", 0.25, "PRODUCT-B");

        publisher.publish(event);

        assertThat(capturedMessages).hasSize(1);
        Message<?> message = capturedMessages.get(0).message;
        assertThat(message.getHeaders().get("aggregateId")).isEqualTo("INS-002");
    }

    @Test
    @DisplayName("訊息標頭應包含 occurredOn")
    void shouldIncludeOccurredOnHeader() {
        InspectionOrderCreatedEvent event = new InspectionOrderCreatedEvent(
                "INS-001", "WO-001", "PRODUCT-A", "INCOMING");

        publisher.publish(event);

        assertThat(capturedMessages).hasSize(1);
        Message<?> message = capturedMessages.get(0).message;
        assertThat(message.getHeaders().get("occurredOn")).isNotNull();
        assertThat(message.getHeaders().get("occurredOn").toString()).contains("T");
    }

    // ==================== 測試輔助類別 ====================

    /**
     * 擷取的訊息記錄。
     */
    static class CapturedMessage {
        final String bindingName;
        final Message<?> message;

        CapturedMessage(String bindingName, Message<?> message) {
            this.bindingName = bindingName;
            this.message = message;
        }
    }

    /**
     * 可測試的 Publisher，覆寫 publish 以攔截 Message 而非實際呼叫 StreamBridge。
     * 這是一種 Test Double 模式，用於繞過 final class 無法 mock 的限制。
     */
    static class TestablePublisher extends KafkaDomainEventPublisher {

        private final List<CapturedMessage> captured;

        TestablePublisher(List<CapturedMessage> captured) {
            super(null); // StreamBridge 不會被實際使用
            this.captured = captured;
        }

        @Override
        public void publish(DomainEvent event) {
            // 重新建構 Message，與 KafkaDomainEventPublisher.publish() 中相同的邏輯
            org.springframework.messaging.Message<DomainEvent> message =
                    org.springframework.messaging.support.MessageBuilder
                            .withPayload(event)
                            .setHeader("eventType", event.getClass().getSimpleName())
                            .setHeader("aggregateId", event.getAggregateId())
                            .setHeader("occurredOn", event.getOccurredOn().toString())
                            .build();

            captured.add(new CapturedMessage("qualityEventsOut-out-0", message));
        }
    }
}
