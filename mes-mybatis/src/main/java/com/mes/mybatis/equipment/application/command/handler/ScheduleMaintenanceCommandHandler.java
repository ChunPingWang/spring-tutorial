package com.mes.mybatis.equipment.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.mybatis.equipment.application.command.ScheduleMaintenanceCommand;
import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.repository.EquipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 排程維護]
 * [SOLID: SRP - 只負責處理 ScheduleMaintenanceCommand]
 *
 * 協調 Domain Model 完成維護排程：
 * 1. 從 Repository 載入 Equipment 聚合根
 * 2. 呼叫聚合根的業務方法安排維護
 * 3. 透過 Repository 持久化更新後的聚合
 */
@Component
public class ScheduleMaintenanceCommandHandler implements CommandHandler<ScheduleMaintenanceCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(ScheduleMaintenanceCommandHandler.class);

    private final EquipmentRepository equipmentRepository;

    public ScheduleMaintenanceCommandHandler(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public Void handle(ScheduleMaintenanceCommand command) {
        log.info("處理排程維護命令: equipmentId={}", command.getEquipmentId());

        EquipmentId equipmentId = EquipmentId.of(command.getEquipmentId());
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipment", command.getEquipmentId()));

        equipment.scheduleMaintenance(command.getDescription(), command.getScheduledDate());
        equipmentRepository.save(equipment);

        log.info("維護排程完成: equipmentId={}", command.getEquipmentId());
        return null;
    }

    @Override
    public Class<ScheduleMaintenanceCommand> getCommandType() {
        return ScheduleMaintenanceCommand.class;
    }
}
