package com.mes.kafka.quality.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import com.mes.kafka.quality.application.command.CreateInspectionCommand;
import com.mes.kafka.quality.domain.model.InspectionOrder;
import com.mes.kafka.quality.domain.model.InspectionOrderId;
import com.mes.kafka.quality.domain.model.InspectionType;
import com.mes.kafka.quality.domain.repository.InspectionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [CQRS Pattern: Command Handler - 建立檢驗工單處理器]
 * [SOLID: SRP - 只負責處理 CreateInspectionCommand]
 * [SOLID: DIP - 依賴 Repository 與 DomainEventPublisher 的抽象]
 * [Hexagonal Architecture: Application Service - 協調領域物件完成用例]
 *
 * 處理建立檢驗工單的命令：
 * 1. 建立 InspectionOrder 聚合根
 * 2. 開始檢驗（觸發狀態轉換與領域事件）
 * 3. 持久化聚合根
 * 4. 發佈領域事件到 Kafka
 */
@Component
public class CreateInspectionCommandHandler implements CommandHandler<CreateInspectionCommand, String> {

    private static final Logger log = LoggerFactory.getLogger(CreateInspectionCommandHandler.class);

    private final InspectionOrderRepository repository;
    private final DomainEventPublisher eventPublisher;

    public CreateInspectionCommandHandler(InspectionOrderRepository repository,
                                          DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String handle(CreateInspectionCommand command) {
        log.info("Handling CreateInspectionCommand: workOrderId={}, productCode={}, type={}",
                command.getWorkOrderId(), command.getProductCode(), command.getType());

        InspectionOrderId id = new InspectionOrderId(UUID.randomUUID().toString());
        InspectionType type = InspectionType.valueOf(command.getType().toUpperCase());

        InspectionOrder order = new InspectionOrder(
                id, command.getWorkOrderId(), command.getProductCode(), type);

        order.startInspection();
        repository.save(order);

        // 發佈領域事件
        for (DomainEvent event : order.getDomainEvents()) {
            eventPublisher.publish(event);
        }
        order.clearEvents();

        log.info("InspectionOrder created: id={}", id.getValue());
        return id.getValue();
    }

    @Override
    public Class<CreateInspectionCommand> getCommandType() {
        return CreateInspectionCommand.class;
    }
}
