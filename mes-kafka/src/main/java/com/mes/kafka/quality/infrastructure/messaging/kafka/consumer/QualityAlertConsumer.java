package com.mes.kafka.quality.infrastructure.messaging.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * [DDD Pattern: Event Consumer - 品質警報事件消費者]
 * [SOLID: SRP - 只負責接收與處理品質警報事件]
 * [Hexagonal Architecture: Input Adapter - 訊息接收配接器]
 *
 * 使用 Spring Cloud Stream 函數式程式設計模型消費品質事件。
 * 訂閱 mes.quality.events 主題，處理品質檢驗相關事件。
 *
 * <p>在實際生產環境中，可能會觸發以下動作：</p>
 * <ul>
 *   <li>發送通知給品質主管</li>
 *   <li>更新 SPC 管制圖</li>
 *   <li>觸發生產線暫停</li>
 * </ul>
 */
@Component
public class QualityAlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(QualityAlertConsumer.class);

    /**
     * 品質事件消費者 Bean。
     * Spring Cloud Stream 會自動綁定到 qualityEventsIn-in-0 binding。
     *
     * @return 消費品質事件的 Consumer 函數
     */
    @Bean
    public Consumer<Message<String>> qualityEventsIn() {
        return message -> {
            String eventType = (String) message.getHeaders().get("eventType");
            String aggregateId = (String) message.getHeaders().get("aggregateId");
            String payload = message.getPayload();

            log.info("Received quality event: type={}, aggregateId={}", eventType, aggregateId);
            log.debug("Quality event payload: {}", payload);

            // 根據事件類型進行不同處理
            if ("QualityAlertEvent".equals(eventType)) {
                log.warn("QUALITY ALERT received! aggregateId={}, payload={}", aggregateId, payload);
                // TODO: 實際生產環境中，此處可觸發通知、暫停生產線等動作
            } else if ("InspectionCompletedEvent".equals(eventType)) {
                log.info("Inspection completed: aggregateId={}", aggregateId);
            } else if ("DefectDetectedEvent".equals(eventType)) {
                log.info("Defect detected: aggregateId={}", aggregateId);
            }
        };
    }
}
