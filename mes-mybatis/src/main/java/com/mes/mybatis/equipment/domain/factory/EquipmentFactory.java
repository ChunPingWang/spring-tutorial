package com.mes.mybatis.equipment.domain.factory;

import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.model.EquipmentType;
import com.mes.mybatis.equipment.domain.model.Location;

/**
 * [DDD Pattern: Factory - 設備工廠]
 *
 * 封裝 Equipment 聚合根的建立邏輯。
 * 確保每次建立的設備都處於一致的初始狀態。
 */
public class EquipmentFactory {

    /**
     * 建立新設備。
     *
     * @param name     設備名稱
     * @param type     設備類型
     * @param location 設備位置
     * @return 新建立的設備聚合根
     */
    public static Equipment create(String name, EquipmentType type, Location location) {
        EquipmentId id = EquipmentId.generate();
        return new Equipment(id, name, type, location);
    }

    /**
     * 使用指定 ID 建立設備（通常用於測試）。
     *
     * @param id       設備 ID
     * @param name     設備名稱
     * @param type     設備類型
     * @param location 設備位置
     * @return 新建立的設備聚合根
     */
    public static Equipment create(EquipmentId id, String name, EquipmentType type, Location location) {
        return new Equipment(id, name, type, location);
    }
}
