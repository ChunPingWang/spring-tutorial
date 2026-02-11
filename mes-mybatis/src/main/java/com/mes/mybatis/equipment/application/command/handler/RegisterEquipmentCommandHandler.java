package com.mes.mybatis.equipment.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.mybatis.equipment.application.command.RegisterEquipmentCommand;
import com.mes.mybatis.equipment.domain.factory.EquipmentFactory;
import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentType;
import com.mes.mybatis.equipment.domain.model.Location;
import com.mes.mybatis.equipment.domain.repository.EquipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 註冊設備]
 * [SOLID: SRP - 只負責處理 RegisterEquipmentCommand]
 *
 * 協調 Domain Model 完成設備註冊流程：
 * 1. 將 Command 的原始資料轉換為領域物件
 * 2. 透過 Factory 建立 Equipment 聚合根
 * 3. 透過 Repository 持久化
 */
@Component
public class RegisterEquipmentCommandHandler implements CommandHandler<RegisterEquipmentCommand, String> {

    private static final Logger log = LoggerFactory.getLogger(RegisterEquipmentCommandHandler.class);

    private final EquipmentRepository equipmentRepository;

    public RegisterEquipmentCommandHandler(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public String handle(RegisterEquipmentCommand command) {
        log.info("處理註冊設備命令: name={}, type={}", command.getName(), command.getType());

        EquipmentType type = EquipmentType.valueOf(command.getType());
        Location location = new Location(
                command.getBuilding(), command.getFloor(),
                command.getZone(), command.getPosition());

        Equipment equipment = EquipmentFactory.create(command.getName(), type, location);
        equipmentRepository.save(equipment);

        log.info("設備註冊完成: id={}", equipment.getId().getValue());
        return equipment.getId().getValue();
    }

    @Override
    public Class<RegisterEquipmentCommand> getCommandType() {
        return RegisterEquipmentCommand.class;
    }
}
