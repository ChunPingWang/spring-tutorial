package com.mes.web.production.application.query;

import com.mes.common.cqrs.Query;
import com.mes.web.production.application.query.dto.ProductionRecordView;

import java.util.List;

/**
 * [CQRS Pattern: Query - 依產線查詢生產紀錄]
 * [SOLID: SRP - 只封裝依產線查詢所需的參數]
 *
 * 支援依產線 ID 查詢生產紀錄，可選擇性地依狀態過濾。
 * status 為 null 時表示不過濾狀態。
 */
public class ListProductionByLineQuery implements Query<List<ProductionRecordView>> {

    private final String lineId;
    private final String status;

    public ListProductionByLineQuery(String lineId, String status) {
        this.lineId = lineId;
        this.status = status;
    }

    public String getLineId() {
        return lineId;
    }

    /**
     * 取得過濾狀態（可選）。
     *
     * @return 狀態字串，若為 null 表示不過濾
     */
    public String getStatus() {
        return status;
    }
}
