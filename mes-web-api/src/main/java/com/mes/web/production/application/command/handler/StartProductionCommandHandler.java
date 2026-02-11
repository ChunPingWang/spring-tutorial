package com.mes.web.production.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.web.production.application.command.StartProductionCommand;
import com.mes.web.production.domain.model.OperatorInfo;
import com.mes.web.production.domain.model.ProductionLine;
import com.mes.web.production.domain.model.ProductionLineId;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionRecordId;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 啟動生產]
 * [SOLID: SRP - 只負責處理 StartProductionCommand]
 * [SOLID: OCP - 新增 Command 只需新增 Handler，不修改此類別]
 * [Hexagonal Architecture: Application Service - 協調 Domain Model 與 Infrastructure]
 *
 * 接收 StartProductionCommand，建立新的 ProductionRecord 聚合根，
 * 啟動生產並透過 Repository 持久化。
 *
 * Handler 的職責：
 * 1. 將 Command 的原始資料轉換為領域物件
 * 2. 呼叫聚合根的業務方法
 * 3. 透過 Repository 儲存結果
 * 4. 回傳操作結果（此處為新建的 ID）
 */
@Component
public class StartProductionCommandHandler
        implements CommandHandler<StartProductionCommand, String> {

    private final ProductionRecordRepository repository;

    public StartProductionCommandHandler(ProductionRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public String handle(StartProductionCommand command) {
        // 1. 建構領域物件
        ProductionRecordId recordId = ProductionRecordId.generate();
        ProductionLineId lineId = ProductionLineId.of(command.getLineId());
        ProductionLine line = new ProductionLine(lineId, command.getLineName());
        OperatorInfo operator = new OperatorInfo(
                command.getOperatorId(),
                command.getOperatorName(),
                command.getShiftCode());

        // 2. 建立聚合根
        ProductionRecord record = new ProductionRecord(
                recordId,
                line,
                command.getWorkOrderId(),
                command.getProductCode(),
                operator);

        // 3. 執行業務操作 - 啟動生產
        record.start();

        // 4. 透過 Repository 持久化
        repository.save(record);

        // 5. 回傳新建的生產紀錄 ID
        return recordId.getValue();
    }

    @Override
    public Class<StartProductionCommand> getCommandType() {
        return StartProductionCommand.class;
    }
}
