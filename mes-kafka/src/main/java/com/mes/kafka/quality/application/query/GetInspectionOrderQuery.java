package com.mes.kafka.quality.application.query;

import com.mes.common.cqrs.Query;
import com.mes.kafka.quality.application.query.dto.InspectionOrderView;

/**
 * [CQRS Pattern: Query - 查詢檢驗工單]
 * [SOLID: SRP - 只攜帶查詢檢驗工單所需的資料]
 *
 * 根據檢驗工單 ID 查詢詳細資訊。
 * 回傳 {@link InspectionOrderView} 唯讀視圖。
 */
public class GetInspectionOrderQuery implements Query<InspectionOrderView> {

    private final String inspectionOrderId;

    public GetInspectionOrderQuery(String inspectionOrderId) {
        this.inspectionOrderId = inspectionOrderId;
    }

    public String getInspectionOrderId() {
        return inspectionOrderId;
    }
}
