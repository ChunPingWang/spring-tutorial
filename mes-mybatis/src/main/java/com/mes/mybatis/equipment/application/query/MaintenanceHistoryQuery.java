package com.mes.mybatis.equipment.application.query;

import com.mes.common.cqrs.Query;
import com.mes.mybatis.equipment.application.query.dto.MaintenanceHistoryView;

import java.util.List;

/**
 * [CQRS Pattern: Query - 查詢維護歷史]
 *
 * 查詢指定設備的所有維護記錄。
 * 此查詢走 CQRS 讀取路徑，直接使用 MyBatis Mapper。
 */
public class MaintenanceHistoryQuery implements Query<List<MaintenanceHistoryView>> {

    private final String equipmentId;

    public MaintenanceHistoryQuery(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }
}
