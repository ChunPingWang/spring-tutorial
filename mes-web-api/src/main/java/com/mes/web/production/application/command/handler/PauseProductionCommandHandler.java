package com.mes.web.production.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.web.production.application.command.PauseProductionCommand;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionRecordId;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 暫停生產]
 * [SOLID: SRP - 只負責處理 PauseProductionCommand]
 * [Hexagonal Architecture: Application Service]
 *
 * 接收 PauseProductionCommand，找到對應的生產紀錄並暫停生產。
 * 聚合根會驗證狀態轉換的合法性。
 */
@Component
public class PauseProductionCommandHandler
        implements CommandHandler<PauseProductionCommand, Void> {

    private final ProductionRecordRepository repository;

    public PauseProductionCommandHandler(ProductionRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(PauseProductionCommand command) {
        // 1. 從 Repository 載入聚合根
        ProductionRecordId recordId = ProductionRecordId.of(command.getProductionRecordId());
        ProductionRecord record = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ProductionRecord", command.getProductionRecordId()));

        // 2. 執行業務操作 - 暫停生產
        record.pause();

        // 3. 持久化變更
        repository.save(record);

        return null;
    }

    @Override
    public Class<PauseProductionCommand> getCommandType() {
        return PauseProductionCommand.class;
    }
}
