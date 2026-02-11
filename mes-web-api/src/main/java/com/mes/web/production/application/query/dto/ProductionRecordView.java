package com.mes.web.production.application.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * [CQRS Pattern: Read Model - 生產紀錄檢視]
 * [SOLID: SRP - 只負責呈現生產紀錄的讀取資料]
 *
 * 生產紀錄的讀取模型 DTO，專為查詢端設計。
 * 在 CQRS 中，讀取模型可以針對 UI 需求自由組合資料，
 * 不受寫入模型（Aggregate Root）結構的限制。
 */
public class ProductionRecordView {

    private String id;
    private String workOrderId;
    private String productCode;
    private String status;
    private String statusDescription;
    private ProductionLineView productionLine;
    private int goodQuantity;
    private int defectiveQuantity;
    private int reworkQuantity;
    private int totalQuantity;
    private BigDecimal yieldRate;
    private String operatorId;
    private String operatorName;
    private String shiftCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductionRecordView() {
    }

    // ========== Getters and Setters ==========

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public ProductionLineView getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(ProductionLineView productionLine) {
        this.productionLine = productionLine;
    }

    public int getGoodQuantity() {
        return goodQuantity;
    }

    public void setGoodQuantity(int goodQuantity) {
        this.goodQuantity = goodQuantity;
    }

    public int getDefectiveQuantity() {
        return defectiveQuantity;
    }

    public void setDefectiveQuantity(int defectiveQuantity) {
        this.defectiveQuantity = defectiveQuantity;
    }

    public int getReworkQuantity() {
        return reworkQuantity;
    }

    public void setReworkQuantity(int reworkQuantity) {
        this.reworkQuantity = reworkQuantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getYieldRate() {
        return yieldRate;
    }

    public void setYieldRate(BigDecimal yieldRate) {
        this.yieldRate = yieldRate;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
