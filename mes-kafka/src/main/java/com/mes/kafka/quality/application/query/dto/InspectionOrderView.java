package com.mes.kafka.quality.application.query.dto;

/**
 * [CQRS Pattern: Read Model / View DTO]
 * [SOLID: ISP - 只包含查詢端需要的欄位，不暴露領域模型內部結構]
 *
 * 檢驗工單唯讀視圖，用於查詢結果的回傳。
 * 與領域模型（InspectionOrder）分離，避免洩漏聚合內部狀態。
 */
public class InspectionOrderView {

    private final String id;
    private final String workOrderId;
    private final String productCode;
    private final String type;
    private final String status;
    private final int resultsCount;
    private final double defectRate;

    public InspectionOrderView(String id, String workOrderId, String productCode,
                                String type, String status, int resultsCount, double defectRate) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.productCode = productCode;
        this.type = type;
        this.status = status;
        this.resultsCount = resultsCount;
        this.defectRate = defectRate;
    }

    public String getId() {
        return id;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getResultsCount() {
        return resultsCount;
    }

    public double getDefectRate() {
        return defectRate;
    }
}
