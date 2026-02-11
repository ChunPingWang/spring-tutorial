package com.mes.kafka.quality.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.kafka.quality.application.command.CompleteInspectionCommand;
import com.mes.kafka.quality.domain.model.InspectionOrder;
import com.mes.kafka.quality.domain.model.InspectionOrderId;
import com.mes.kafka.quality.domain.repository.InspectionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 完成檢驗處理器]
 * [SOLID: SRP - 只負責處理 CompleteInspectionCommand]
 * [SOLID: DIP - 依賴 Repository 與 DomainEventPublisher 的抽象]
 * [Hexagonal Architecture: Application Service]
 *
 * 處理完成檢驗的命令：
 * 1. 載入既有的 InspectionOrder
 * 2. 執行完成操作（判定 PASSED/FAILED，計算不良率）
 * 3. 持久化並發佈領域事件（含 QualityAlertEvent 若不良率超標）
 */
@Component
public class CompleteInspectionCommandHandler implements CommandHandler<CompleteInspectionCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(CompleteInspectionCommandHandler.class);

    private final InspectionOrderRepository repository;
    private final DomainEventPublisher eventPublisher;

    public CompleteInspectionCommandHandler(InspectionOrderRepository repository,
                                             DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Void handle(CompleteInspectionCommand command) {
        log.info("Handling CompleteInspectionCommand: inspectionOrderId={}", command.getInspectionOrderId());

        InspectionOrderId orderId = new InspectionOrderId(command.getInspectionOrderId());
        InspectionOrder order = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("InspectionOrder", command.getInspectionOrderId()));

        order.complete();
        repository.save(order);

        // 發佈領域事件（可能包含 InspectionCompletedEvent 及 QualityAlertEvent）
        for (DomainEvent event : order.getDomainEvents()) {
            eventPublisher.publish(event);
            log.info("Published domain event: {}", event.getClass().getSimpleName());
        }
        order.clearEvents();

        log.info("InspectionOrder completed: id={}, status={}, defectRate={}",
                orderId.getValue(), order.getStatus(), order.getDefectRate());
        return null;
    }

    @Override
    public Class<CompleteInspectionCommand> getCommandType() {
        return CompleteInspectionCommand.class;
    }
}
