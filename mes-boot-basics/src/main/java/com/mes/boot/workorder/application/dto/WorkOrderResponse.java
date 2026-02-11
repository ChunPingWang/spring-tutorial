package com.mes.boot.workorder.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [DDD Pattern: DTO (Data Transfer Object)]
 * [SOLID: SRP - 只負責攜帶工單回應的資料]
 *
 * 工單回應 DTO，用於應用層向外部傳遞工單資料。
 * 將領域模型的複雜結構扁平化為簡單的資料結構。
 *
 * 注意：DTO 不包含任何領域邏輯，只是資料的純粹映射。
 * 領域模型與 DTO 之間的轉換由 {@link com.mes.boot.workorder.application.assembler.WorkOrderAssembler} 負責。
 */
public class WorkOrderResponse {

    private String id;
    private String status;
    private String productCode;
    private String productName;
    private int planned;
    private int completed;
    private int defective;
    private String priority;
    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    private LocalDateTime createdAt;

    public WorkOrderResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getPlanned() {
        return planned;
    }

    public void setPlanned(int planned) {
        this.planned = planned;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getDefective() {
        return defective;
    }

    public void setDefective(int defective) {
        this.defective = defective;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "WorkOrderResponse{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", productCode='" + productCode + '\'' +
                ", planned=" + planned +
                ", completed=" + completed +
                ", priority='" + priority + '\'' +
                '}';
    }
}
