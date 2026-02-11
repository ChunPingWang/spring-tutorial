package com.mes.mybatis.equipment.application.query.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [CQRS Pattern: Read Model DTO - 維護記錄視圖]
 *
 * 維護記錄的展示用資料結構，扁平化設計便於前端使用。
 */
public class MaintenanceHistoryView {

    private String id;
    private String equipmentId;
    private String maintenanceType;
    private String description;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private String technicianName;
    private String status;
    private LocalDateTime createdAt;

    public MaintenanceHistoryView() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
