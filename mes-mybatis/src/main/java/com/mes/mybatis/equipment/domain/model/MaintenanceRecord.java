package com.mes.mybatis.equipment.domain.model;

import com.mes.common.ddd.model.BaseEntity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * [DDD Pattern: Entity - 維護記錄]
 *
 * 歸屬於 Equipment 聚合的內部 Entity。
 * 只能透過 Equipment 聚合根來建立與修改，不可直接從外部存取。
 */
public class MaintenanceRecord extends BaseEntity<MaintenanceRecordId> {

    private String equipmentId;
    private String maintenanceType;
    private String description;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private String technicianName;
    private String status;

    protected MaintenanceRecord() {
        super();
    }

    public MaintenanceRecord(MaintenanceRecordId id, String equipmentId,
                             String maintenanceType, String description,
                             LocalDate scheduledDate) {
        super(id);
        this.equipmentId = Objects.requireNonNull(equipmentId, "Equipment ID must not be null");
        this.maintenanceType = Objects.requireNonNull(maintenanceType, "Maintenance type must not be null");
        this.description = Objects.requireNonNull(description, "Description must not be null");
        this.scheduledDate = Objects.requireNonNull(scheduledDate, "Scheduled date must not be null");
        this.status = "SCHEDULED";
    }

    /**
     * 完成維護記錄。
     */
    void complete(String technicianName) {
        this.technicianName = Objects.requireNonNull(technicianName, "Technician name must not be null");
        this.completedDate = LocalDate.now();
        this.status = "COMPLETED";
        touch();
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public String getStatus() {
        return status;
    }

    // ---- 供 Converter 重建領域物件使用的 setter ----
    // 設為 public 以允許基礎設施層的 Converter 跨套件存取

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 供 Converter 設定建立時間（覆寫 protected 方法提升為 public）。
     */
    @Override
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        super.setCreatedAt(createdAt);
    }
}
