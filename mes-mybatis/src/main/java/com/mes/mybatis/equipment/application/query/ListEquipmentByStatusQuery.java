package com.mes.mybatis.equipment.application.query;

import com.mes.common.cqrs.Query;
import com.mes.mybatis.equipment.application.query.dto.EquipmentSummaryView;

import java.util.List;

/**
 * [CQRS Pattern: Query - 依狀態列出設備摘要]
 *
 * 查詢指定狀態的所有設備，返回摘要資訊。
 * 此查詢走 CQRS 讀取路徑，直接使用 MyBatis Mapper 而非通過 Domain Model。
 */
public class ListEquipmentByStatusQuery implements Query<List<EquipmentSummaryView>> {

    private final String status;

    public ListEquipmentByStatusQuery(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
