package com.mes.web.production.application.query;

import com.mes.common.cqrs.Query;
import com.mes.web.production.application.query.dto.ProductionRecordView;

/**
 * [CQRS Pattern: Query - 取得單筆生產紀錄]
 * [SOLID: SRP - 只封裝查詢單筆生產紀錄所需的參數]
 *
 * Query 是不可變的資料容器，不包含任何業務邏輯。
 * 型別參數 ProductionRecordView 指定了查詢結果的型別。
 */
public class GetProductionRecordQuery implements Query<ProductionRecordView> {

    private final String id;

    public GetProductionRecordQuery(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
