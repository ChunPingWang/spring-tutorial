package com.mes.kafka.quality.application.query;

import com.mes.common.cqrs.Query;
import com.mes.kafka.quality.application.query.dto.DefectStatisticsView;

/**
 * [CQRS Pattern: Query - 查詢缺陷統計]
 * [SOLID: SRP - 只攜帶查詢缺陷統計所需的資料]
 *
 * 查詢品質缺陷統計資訊。
 * 可選擇性地依產品代碼篩選。
 * 回傳 {@link DefectStatisticsView} 唯讀視圖。
 */
public class DefectStatisticsQuery implements Query<DefectStatisticsView> {

    private final String productCode;

    /**
     * 查詢所有產品的缺陷統計。
     */
    public DefectStatisticsQuery() {
        this.productCode = null;
    }

    /**
     * 查詢指定產品的缺陷統計。
     *
     * @param productCode 產品代碼（可為 null 表示查詢所有）
     */
    public DefectStatisticsQuery(String productCode) {
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }
}
