package com.mes.kafka.quality.infrastructure.messaging.kafka.consumer;

import com.mes.kafka.quality.application.command.CreateInspectionCommand;
import com.mes.kafka.quality.application.command.handler.CreateInspectionCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * [DDD Pattern: Event Consumer - 跨 Bounded Context 事件消費者]
 * [SOLID: SRP - 只負責接收生產完成事件並觸發品質檢驗]
 * [SOLID: DIP - 依賴 CommandHandler 抽象進行後續處理]
 * [Hexagonal Architecture: Input Adapter - 訊息接收配接器]
 *
 * 消費來自生產 Bounded Context 的事件（mes.production.events）。
 * 這是跨 Bounded Context 整合的典型範例：
 * 當生產完成事件發生時，自動建立最終檢驗（FINAL）工單。
 *
 * <p>展示事件驅動架構中的關鍵概念：</p>
 * <ul>
 *   <li>Bounded Context 之間透過事件解耦</li>
 *   <li>最終一致性（Eventually Consistent）</li>
 *   <li>消費者將外部事件轉換為內部命令（Anti-Corruption Layer 概念）</li>
 * </ul>
 */
@Component
public class ProductionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductionEventConsumer.class);

    private final CreateInspectionCommandHandler createInspectionCommandHandler;

    public ProductionEventConsumer(CreateInspectionCommandHandler createInspectionCommandHandler) {
        this.createInspectionCommandHandler = createInspectionCommandHandler;
    }

    /**
     * 生產事件消費者 Bean。
     * Spring Cloud Stream 會自動綁定到 productionEventsIn-in-0 binding。
     * 接收生產完成事件後，自動建立 FINAL 類型的檢驗工單。
     *
     * @return 消費生產事件的 Consumer 函數
     */
    @Bean
    public Consumer<Message<String>> productionEventsIn() {
        return message -> {
            String eventType = (String) message.getHeaders().get("eventType");
            String payload = message.getPayload();

            log.info("Received production event: type={}, payload={}", eventType, payload);

            if ("ProductionCompletedEvent".equals(eventType)) {
                handleProductionCompleted(message);
            } else {
                log.debug("Ignoring production event type: {}", eventType);
            }
        };
    }

    /**
     * 處理生產完成事件。
     * 從事件訊息中擷取工單 ID 和產品代碼，建立最終檢驗工單。
     */
    private void handleProductionCompleted(Message<String> message) {
        // 從 headers 或 payload 中取得必要資訊
        String workOrderId = (String) message.getHeaders().get("aggregateId");
        String productCode = (String) message.getHeaders().getOrDefault("productCode", "UNKNOWN");

        log.info("Production completed for workOrder={}, creating FINAL inspection order", workOrderId);

        try {
            CreateInspectionCommand command = new CreateInspectionCommand(
                    workOrderId, productCode, "FINAL");
            String inspectionOrderId = createInspectionCommandHandler.handle(command);
            log.info("Auto-created FINAL inspection order: {} for workOrder: {}",
                    inspectionOrderId, workOrderId);
        } catch (Exception e) {
            log.error("Failed to auto-create inspection order for workOrder: {}", workOrderId, e);
            // 在實際生產環境中，應該將失敗的事件發送到 DLQ（Dead Letter Queue）
        }
    }
}
