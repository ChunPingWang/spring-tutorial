package com.mes.mybatis.equipment.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.mybatis.equipment.application.command.ReportBreakdownCommand;
import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.repository.EquipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 回報故障]
 * [SOLID: SRP - 只負責處理 ReportBreakdownCommand]
 *
 * 協調 Domain Model 完成故障回報：
 * 1. 從 Repository 載入 Equipment 聚合根
 * 2. 呼叫聚合根的業務方法回報故障
 * 3. 透過 Repository 持久化更新後的聚合
 */
@Component
public class ReportBreakdownCommandHandler implements CommandHandler<ReportBreakdownCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(ReportBreakdownCommandHandler.class);

    private final EquipmentRepository equipmentRepository;

    public ReportBreakdownCommandHandler(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public Void handle(ReportBreakdownCommand command) {
        log.info("處理回報故障命令: equipmentId={}", command.getEquipmentId());

        EquipmentId equipmentId = EquipmentId.of(command.getEquipmentId());
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipment", command.getEquipmentId()));

        equipment.reportBreakdown(command.getDescription());
        equipmentRepository.save(equipment);

        log.info("故障回報完成: equipmentId={}", command.getEquipmentId());
        return null;
    }

    @Override
    public Class<ReportBreakdownCommand> getCommandType() {
        return ReportBreakdownCommand.class;
    }
}
