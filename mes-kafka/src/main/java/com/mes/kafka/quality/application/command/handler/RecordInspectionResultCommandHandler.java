package com.mes.kafka.quality.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.kafka.quality.application.command.RecordInspectionResultCommand;
import com.mes.kafka.quality.domain.model.InspectionOrder;
import com.mes.kafka.quality.domain.model.InspectionOrderId;
import com.mes.kafka.quality.domain.model.MeasuredValue;
import com.mes.kafka.quality.domain.model.QualityStandard;
import com.mes.kafka.quality.domain.repository.InspectionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * [CQRS Pattern: Command Handler - 記錄檢驗結果處理器]
 * [SOLID: SRP - 只負責處理 RecordInspectionResultCommand]
 * [SOLID: DIP - 依賴 Repository 與 DomainEventPublisher 的抽象]
 * [Hexagonal Architecture: Application Service]
 *
 * 處理記錄檢驗結果的命令：
 * 1. 載入既有的 InspectionOrder
 * 2. 建立 QualityStandard 與 MeasuredValue
 * 3. 透過聚合根記錄結果
 * 4. 持久化並發佈事件
 */
@Component
public class RecordInspectionResultCommandHandler implements CommandHandler<RecordInspectionResultCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(RecordInspectionResultCommandHandler.class);

    private final InspectionOrderRepository repository;
    private final DomainEventPublisher eventPublisher;

    public RecordInspectionResultCommandHandler(InspectionOrderRepository repository,
                                                 DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Void handle(RecordInspectionResultCommand command) {
        log.info("Handling RecordInspectionResultCommand: inspectionOrderId={}, standardCode={}",
                command.getInspectionOrderId(), command.getStandardCode());

        InspectionOrderId orderId = new InspectionOrderId(command.getInspectionOrderId());
        InspectionOrder order = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("InspectionOrder", command.getInspectionOrderId()));

        QualityStandard standard = new QualityStandard(
                command.getStandardCode(),
                command.getLowerBound(),
                command.getUpperBound(),
                command.getUnit());

        MeasuredValue measuredValue = new MeasuredValue(
                command.getMeasuredValue(),
                command.getMeasuredUnit(),
                LocalDateTime.now(),
                command.getInspector());

        order.recordResult(standard, measuredValue);
        repository.save(order);

        // 發佈領域事件
        for (DomainEvent event : order.getDomainEvents()) {
            eventPublisher.publish(event);
        }
        order.clearEvents();

        log.info("Inspection result recorded for order: {}", orderId.getValue());
        return null;
    }

    @Override
    public Class<RecordInspectionResultCommand> getCommandType() {
        return RecordInspectionResultCommand.class;
    }
}
