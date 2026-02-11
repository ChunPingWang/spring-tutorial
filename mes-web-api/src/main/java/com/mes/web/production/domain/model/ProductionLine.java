package com.mes.web.production.domain.model;

import com.mes.common.ddd.model.BaseEntity;
import com.mes.common.exception.DomainException;

/**
 * [DDD Pattern: Entity - 產線]
 * [SOLID: SRP - 只負責產線的身份識別與基本屬性]
 *
 * ProductionLine 是聚合內部的 Entity，由 ProductionRecord (Aggregate Root) 管理。
 * 具有唯一識別 (ProductionLineId)，兩條產線即使名稱相同，ID 不同就視為不同產線。
 */
public class ProductionLine extends BaseEntity<ProductionLineId> {

    private final ProductionLineId lineId;
    private final String lineName;

    public ProductionLine(ProductionLineId lineId, String lineName) {
        super(lineId);
        if (lineName == null || lineName.trim().isEmpty()) {
            throw new DomainException("產線名稱不可為空");
        }
        this.lineId = lineId;
        this.lineName = lineName;
    }

    public ProductionLineId getLineId() {
        return lineId;
    }

    public String getLineName() {
        return lineName;
    }

    @Override
    public String toString() {
        return "ProductionLine{lineId=" + lineId + ", lineName='" + lineName + "'}";
    }
}
