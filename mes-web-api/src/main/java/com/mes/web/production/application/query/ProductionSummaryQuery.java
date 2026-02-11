package com.mes.web.production.application.query;

import com.mes.common.cqrs.Query;
import com.mes.web.production.application.query.dto.ProductionSummaryView;

/**
 * [CQRS Pattern: Query - 生產摘要查詢]
 * [SOLID: SRP - 只封裝生產摘要查詢的意圖]
 *
 * 查詢整體生產摘要統計資料。
 * 此查詢不需要任何參數，回傳所有生產紀錄的彙總資訊。
 */
public class ProductionSummaryQuery implements Query<ProductionSummaryView> {

    public ProductionSummaryQuery() {
    }
}
