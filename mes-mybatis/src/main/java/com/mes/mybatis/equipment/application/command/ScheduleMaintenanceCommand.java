package com.mes.mybatis.equipment.application.command;

import com.mes.common.cqrs.Command;

import java.time.LocalDate;

/**
 * [CQRS Pattern: Command - 排程維護命令]
 *
 * 代表「為設備安排一次維護」的意圖。
 */
public class ScheduleMaintenanceCommand implements Command {

    private final String equipmentId;
    private final String description;
    private final LocalDate scheduledDate;

    public ScheduleMaintenanceCommand(String equipmentId, String description, LocalDate scheduledDate) {
        this.equipmentId = equipmentId;
        this.description = description;
        this.scheduledDate = scheduledDate;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
}
