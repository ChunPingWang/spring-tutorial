package com.mes.mybatis.equipment.application.command;

import com.mes.common.cqrs.Command;

/**
 * [CQRS Pattern: Command - 註冊設備命令]
 *
 * 代表「註冊一台新設備」的意圖。
 * Command 是不可變的資料載體，只攜帶執行操作所需的資訊。
 */
public class RegisterEquipmentCommand implements Command {

    private final String name;
    private final String type;
    private final String building;
    private final String floor;
    private final String zone;
    private final String position;

    public RegisterEquipmentCommand(String name, String type,
                                    String building, String floor,
                                    String zone, String position) {
        this.name = name;
        this.type = type;
        this.building = building;
        this.floor = floor;
        this.zone = zone;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBuilding() {
        return building;
    }

    public String getFloor() {
        return floor;
    }

    public String getZone() {
        return zone;
    }

    public String getPosition() {
        return position;
    }
}
