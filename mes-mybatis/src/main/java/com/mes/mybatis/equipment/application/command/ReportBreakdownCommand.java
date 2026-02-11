package com.mes.mybatis.equipment.application.command;

import com.mes.common.cqrs.Command;

/**
 * [CQRS Pattern: Command - 回報設備故障命令]
 *
 * 代表「回報一台設備發生故障」的意圖。
 */
public class ReportBreakdownCommand implements Command {

    private final String equipmentId;
    private final String description;

    public ReportBreakdownCommand(String equipmentId, String description) {
        this.equipmentId = equipmentId;
        this.description = description;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getDescription() {
        return description;
    }
}
