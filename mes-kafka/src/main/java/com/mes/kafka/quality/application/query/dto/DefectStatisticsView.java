package com.mes.kafka.quality.application.query.dto;

import java.util.List;

/**
 * [CQRS Pattern: Read Model / View DTO]
 * [SOLID: ISP - 只包含缺陷統計需要的欄位]
 *
 * 缺陷統計唯讀視圖，用於查詢結果的回傳。
 * 彙整所有檢驗工單的缺陷資訊，提供整體品質概覽。
 */
public class DefectStatisticsView {

    private final int totalInspections;
    private final int totalDefects;
    private final double overallDefectRate;
    private final List<String> topDefectCodes;

    public DefectStatisticsView(int totalInspections, int totalDefects,
                                 double overallDefectRate, List<String> topDefectCodes) {
        this.totalInspections = totalInspections;
        this.totalDefects = totalDefects;
        this.overallDefectRate = overallDefectRate;
        this.topDefectCodes = topDefectCodes;
    }

    public int getTotalInspections() {
        return totalInspections;
    }

    public int getTotalDefects() {
        return totalDefects;
    }

    public double getOverallDefectRate() {
        return overallDefectRate;
    }

    public List<String> getTopDefectCodes() {
        return topDefectCodes;
    }
}
