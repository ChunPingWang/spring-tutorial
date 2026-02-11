package com.mes.web.production.application.query.dto;

/**
 * [CQRS Pattern: Read Model - 產線資訊檢視]
 * [SOLID: SRP - 只負責呈現產線的讀取資料]
 *
 * 產線資訊的讀取模型 DTO。
 */
public class ProductionLineView {

    private String lineId;
    private String lineName;

    public ProductionLineView() {
    }

    public ProductionLineView(String lineId, String lineName) {
        this.lineId = lineId;
        this.lineName = lineName;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
}
