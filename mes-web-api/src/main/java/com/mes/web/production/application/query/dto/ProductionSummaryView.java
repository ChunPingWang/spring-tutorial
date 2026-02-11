package com.mes.web.production.application.query.dto;

import java.math.BigDecimal;

/**
 * [CQRS Pattern: Read Model - 生產摘要檢視]
 * [SOLID: SRP - 只負責呈現生產摘要統計資料]
 *
 * 生產摘要的讀取模型 DTO，包含整體統計資訊。
 * 此 DTO 整合了來自多筆生產紀錄的彙總資料，
 * 是 CQRS 讀取模型可以自由組合資料的最佳展示。
 */
public class ProductionSummaryView {

    private int totalRecords;
    private int totalGood;
    private int totalDefective;
    private BigDecimal overallYieldRate;

    public ProductionSummaryView() {
    }

    public ProductionSummaryView(int totalRecords, int totalGood,
                                  int totalDefective, BigDecimal overallYieldRate) {
        this.totalRecords = totalRecords;
        this.totalGood = totalGood;
        this.totalDefective = totalDefective;
        this.overallYieldRate = overallYieldRate;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalGood() {
        return totalGood;
    }

    public void setTotalGood(int totalGood) {
        this.totalGood = totalGood;
    }

    public int getTotalDefective() {
        return totalDefective;
    }

    public void setTotalDefective(int totalDefective) {
        this.totalDefective = totalDefective;
    }

    public BigDecimal getOverallYieldRate() {
        return overallYieldRate;
    }

    public void setOverallYieldRate(BigDecimal overallYieldRate) {
        this.overallYieldRate = overallYieldRate;
    }
}
