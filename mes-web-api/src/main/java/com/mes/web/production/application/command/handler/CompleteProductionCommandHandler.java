package com.mes.web.production.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.web.production.application.command.CompleteProductionCommand;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionRecordId;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 完成生產]
 * [SOLID: SRP - 只負責處理 CompleteProductionCommand]
 * [Hexagonal Architecture: Application Service]
 *
 * 接收 CompleteProductionCommand，找到對應的生產紀錄並完成生產。
 * 聚合根會驗證狀態轉換的合法性，並註冊 ProductionCompletedEvent。
 */
@Component
public class CompleteProductionCommandHandler
        implements CommandHandler<CompleteProductionCommand, Void> {

    private final ProductionRecordRepository repository;

    public CompleteProductionCommandHandler(ProductionRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(CompleteProductionCommand command) {
        // 1. 從 Repository 載入聚合根
        ProductionRecordId recordId = ProductionRecordId.of(command.getProductionRecordId());
        ProductionRecord record = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ProductionRecord", command.getProductionRecordId()));

        // 2. 執行業務操作 - 完成生產
        record.finish();

        // 3. 持久化變更
        repository.save(record);

        return null;
    }

    @Override
    public Class<CompleteProductionCommand> getCommandType() {
        return CompleteProductionCommand.class;
    }
}
