package com.mes.web.production.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.web.production.application.command.RecordOutputCommand;
import com.mes.web.production.domain.model.OutputQuantity;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionRecordId;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 記錄產出]
 * [SOLID: SRP - 只負責處理 RecordOutputCommand]
 * [Hexagonal Architecture: Application Service]
 *
 * 接收 RecordOutputCommand，找到對應的生產紀錄並更新產出數量。
 * 若有不良品，聚合根會自動註冊 DefectRecordedEvent。
 */
@Component
public class RecordOutputCommandHandler
        implements CommandHandler<RecordOutputCommand, Void> {

    private final ProductionRecordRepository repository;

    public RecordOutputCommandHandler(ProductionRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(RecordOutputCommand command) {
        // 1. 從 Repository 載入聚合根
        ProductionRecordId recordId = ProductionRecordId.of(command.getProductionRecordId());
        ProductionRecord record = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ProductionRecord", command.getProductionRecordId()));

        // 2. 建構 Value Object 並執行業務操作
        OutputQuantity output = new OutputQuantity(
                command.getGood(),
                command.getDefective(),
                command.getRework());
        record.recordOutput(output);

        // 3. 持久化變更
        repository.save(record);

        return null;
    }

    @Override
    public Class<RecordOutputCommand> getCommandType() {
        return RecordOutputCommand.class;
    }
}
