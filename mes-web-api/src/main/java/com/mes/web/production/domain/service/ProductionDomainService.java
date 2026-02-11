package com.mes.web.production.domain.service;

import com.mes.common.ddd.annotation.DomainService;
import com.mes.web.production.domain.model.OutputQuantity;
import com.mes.web.production.domain.model.ProductionRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * [DDD Pattern: Domain Service - 生產領域服務]
 * [SOLID: SRP - 負責跨聚合的生產統計計算]
 *
 * Domain Service 封裝不屬於任何單一 Aggregate 的領域邏輯。
 * 本服務提供跨多筆生產紀錄的統計計算功能：
 * - 整體良率計算
 * - 產量（吞吐量）計算
 *
 * 注意：Domain Service 是無狀態的，所有資料都透過參數傳入。
 */
@DomainService
public class ProductionDomainService {

    /**
     * 計算多筆生產紀錄的整體良率。
     * 整體良率 = 總良品數 / 總產出數 * 100
     *
     * @param records 生產紀錄列表
     * @return 整體良率百分比（0 ~ 100），若無產出則回傳 0
     */
    public BigDecimal calculateYieldRate(List<ProductionRecord> records) {
        if (records == null || records.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int totalGood = 0;
        int totalAll = 0;

        for (ProductionRecord record : records) {
            OutputQuantity output = record.getOutput();
            totalGood += output.getGood();
            totalAll += output.getTotal();
        }

        if (totalAll == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(totalGood)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalAll), 2, RoundingMode.HALF_UP);
    }

    /**
     * 計算多筆生產紀錄的總產量（吞吐量）。
     * 吞吐量 = 所有紀錄的總產出數量之和。
     *
     * @param records 生產紀錄列表
     * @return 總產量
     */
    public int calculateThroughput(List<ProductionRecord> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }

        int throughput = 0;
        for (ProductionRecord record : records) {
            throughput += record.getOutput().getTotal();
        }
        return throughput;
    }
}
