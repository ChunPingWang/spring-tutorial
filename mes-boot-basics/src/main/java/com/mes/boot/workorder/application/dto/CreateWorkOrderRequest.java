package com.mes.boot.workorder.application.dto;

import java.time.LocalDate;

/**
 * [DDD Pattern: DTO (Data Transfer Object)]
 * [SOLID: SRP - 只負責攜帶建立工單請求的資料]
 *
 * 建立工單請求 DTO，用於應用層接收外部輸入。
 * DTO 是純粹的資料載體，不包含任何領域邏輯。
 *
 * 在六角架構中，DTO 屬於應用層的入站適配器（Input Adapter）資料結構。
 */
public class CreateWorkOrderRequest {

    private String productCode;
    private String productName;
    private String specification;
    private int plannedQuantity;
    private String priority;
    private LocalDate plannedStart;
    private LocalDate plannedEnd;

    public CreateWorkOrderRequest() {
    }

    public CreateWorkOrderRequest(String productCode, String productName, String specification,
                                  int plannedQuantity, String priority,
                                  LocalDate plannedStart, LocalDate plannedEnd) {
        this.productCode = productCode;
        this.productName = productName;
        this.specification = specification;
        this.plannedQuantity = plannedQuantity;
        this.priority = priority;
        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public int getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(int plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDate plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDate getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(LocalDate plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    @Override
    public String toString() {
        return "CreateWorkOrderRequest{" +
                "productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", plannedQuantity=" + plannedQuantity +
                ", priority='" + priority + '\'' +
                '}';
    }
}
