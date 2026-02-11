package com.mes.kafka.quality.domain.service;

import com.mes.common.ddd.annotation.DomainService;
import com.mes.kafka.quality.domain.model.InspectionResult;
import com.mes.kafka.quality.domain.model.MeasuredValue;
import com.mes.kafka.quality.domain.model.QualityStandard;

import java.util.List;

/**
 * [DDD Pattern: Domain Service - 品質領域服務]
 * [SOLID: SRP - 只負責跨實體的品質計算邏輯]
 * [SOLID: OCP - 可擴展新的統計方法而不修改現有邏輯]
 *
 * 品質領域服務，封裝不屬於任何單一 Entity 的品質領域邏輯。
 * 提供品質統計與 SPC（Statistical Process Control）分析方法。
 *
 * <p>包含功能：</p>
 * <ul>
 *   <li>不良率計算</li>
 *   <li>SPC 管制圖分析（簡化版：連續7點同側規則）</li>
 * </ul>
 */
@DomainService
public class QualityDomainService {

    /**
     * 計算一組檢驗結果的不良率。
     *
     * @param results 檢驗結果列表
     * @return 不良率（0.0 ~ 1.0），若列表為空則返回 0.0
     */
    public double calculateDefectRate(List<InspectionResult> results) {
        if (results == null || results.isEmpty()) {
            return 0.0;
        }
        long failedCount = 0;
        for (InspectionResult result : results) {
            if (!result.isPassed()) {
                failedCount++;
            }
        }
        return (double) failedCount / results.size();
    }

    /**
     * 簡化版 SPC（統計製程控制）分析。
     * 檢查量測值序列中是否存在連續 7 個值落在平均值的同一側，
     * 這是 Western Electric Rules 中的基本規則之一。
     *
     * <p>判定邏輯：</p>
     * <ul>
     *   <li>計算品質標準的中心值（mean = (upperBound + lowerBound) / 2）</li>
     *   <li>若連續 7 個量測值都大於 mean 或都小於 mean，則判定製程失控</li>
     * </ul>
     *
     * @param values   量測值列表
     * @param standard 品質標準（用於計算中心值）
     * @return true 表示製程在管制範圍內（正常），false 表示偵測到異常趨勢
     */
    public boolean isWithinSPC(List<MeasuredValue> values, QualityStandard standard) {
        if (values == null || values.size() < 7) {
            return true;
        }

        double mean = standard.getMean();
        int consecutiveAbove = 0;
        int consecutiveBelow = 0;

        for (MeasuredValue mv : values) {
            if (mv.getValue() > mean) {
                consecutiveAbove++;
                consecutiveBelow = 0;
            } else if (mv.getValue() < mean) {
                consecutiveBelow++;
                consecutiveAbove = 0;
            } else {
                // 恰好等於 mean，重置兩個計數器
                consecutiveAbove = 0;
                consecutiveBelow = 0;
            }

            if (consecutiveAbove >= 7 || consecutiveBelow >= 7) {
                return false;
            }
        }

        return true;
    }
}
