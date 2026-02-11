package com.mes.mybatis.equipment.application.query;

import com.mes.common.cqrs.Query;
import com.mes.mybatis.equipment.application.query.dto.EquipmentDetailView;

/**
 * [CQRS Pattern: Query - 查詢單一設備詳情]
 *
 * 查詢指定 ID 的設備完整資訊，包含維護記錄。
 */
public class GetEquipmentQuery implements Query<EquipmentDetailView> {

    private final String equipmentId;

    public GetEquipmentQuery(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }
}
