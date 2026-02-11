package com.mes.mybatis.equipment.domain.model;

import com.mes.common.ddd.annotation.AggregateRoot;
import com.mes.common.ddd.model.BaseAggregateRoot;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.mybatis.equipment.domain.event.EquipmentBreakdownEvent;
import com.mes.mybatis.equipment.domain.event.MaintenanceCompletedEvent;
import com.mes.mybatis.equipment.domain.event.MaintenanceScheduledEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Aggregate Root - 設備]
 * [Hexagonal Architecture: Domain Model - 核心業務邏輯不依賴任何外部框架]
 *
 * Equipment 是設備管理的聚合根，負責：
 * 1. 維護設備狀態轉換的業務規則（狀態機）
 * 2. 管理維護記錄（內部 Entity）
 * 3. 在關鍵業務操作時註冊領域事件
 *
 * 所有狀態變更都必須透過此聚合根的方法進行，確保一致性。
 */
@AggregateRoot
public class Equipment extends BaseAggregateRoot<EquipmentId> {

    private String name;
    private EquipmentType type;
    private EquipmentStatus status;
    private Location location;
    private OperatingParameters operatingParameters;
    private final List<MaintenanceRecord> maintenanceRecords;

    /**
     * 供 ORM / Converter 重建使用的無參建構子。
     */
    protected Equipment() {
        super();
        this.maintenanceRecords = new ArrayList<>();
    }

    /**
     * 建立新設備（正常建構路徑）。
     */
    public Equipment(EquipmentId id, String name, EquipmentType type,
                     Location location) {
        super(id);
        this.name = Objects.requireNonNull(name, "Equipment name must not be null");
        this.type = Objects.requireNonNull(type, "Equipment type must not be null");
        this.status = EquipmentStatus.IDLE;
        this.location = Objects.requireNonNull(location, "Location must not be null");
        this.operatingParameters = OperatingParameters.defaultParameters();
        this.maintenanceRecords = new ArrayList<>();
    }

    // ======================== 狀態轉換業務方法 ========================

    /**
     * 啟動設備運行。
     * 規則：只有 IDLE 狀態的設備才能啟動。
     */
    public void startRunning() {
        if (this.status != EquipmentStatus.IDLE) {
            throw new DomainException(
                    String.format("Cannot start equipment [%s]: current status is %s, expected IDLE",
                            getId().getValue(), this.status));
        }
        this.status = EquipmentStatus.RUNNING;
        touch();
    }

    /**
     * 停止設備運行。
     * 規則：只有 RUNNING 狀態的設備才能停止。
     */
    public void stopRunning() {
        if (this.status != EquipmentStatus.RUNNING) {
            throw new DomainException(
                    String.format("Cannot stop equipment [%s]: current status is %s, expected RUNNING",
                            getId().getValue(), this.status));
        }
        this.status = EquipmentStatus.IDLE;
        touch();
    }

    /**
     * 報告設備故障。
     * 設定狀態為 BREAKDOWN 並註冊故障事件。
     *
     * @param description 故障描述
     */
    public void reportBreakdown(String description) {
        if (this.status == EquipmentStatus.DECOMMISSIONED) {
            throw new BusinessRuleViolationException(
                    String.format("Cannot report breakdown for decommissioned equipment [%s]",
                            getId().getValue()));
        }
        this.status = EquipmentStatus.BREAKDOWN;
        touch();
        registerEvent(new EquipmentBreakdownEvent(getId().getValue(), name, description));
    }

    /**
     * 安排維護。
     * 規則：已報廢的設備不能安排維護。
     *
     * @param description   維護描述
     * @param scheduledDate 預定日期
     */
    public void scheduleMaintenance(String description, LocalDate scheduledDate) {
        if (this.status == EquipmentStatus.DECOMMISSIONED) {
            throw new BusinessRuleViolationException(
                    String.format("Cannot schedule maintenance for decommissioned equipment [%s]",
                            getId().getValue()));
        }

        MaintenanceRecordId recordId = MaintenanceRecordId.generate();
        MaintenanceRecord record = new MaintenanceRecord(
                recordId, getId().getValue(), "PREVENTIVE", description, scheduledDate);
        this.maintenanceRecords.add(record);
        this.status = EquipmentStatus.MAINTENANCE;
        touch();

        registerEvent(new MaintenanceScheduledEvent(
                getId().getValue(), name, recordId.getValue(), scheduledDate));
    }

    /**
     * 完成維護。
     * 將指定維護記錄標記為完成，並將設備狀態設為 IDLE。
     *
     * @param recordId       維護記錄 ID
     * @param technicianName 技術人員姓名
     */
    public void completeMaintenance(MaintenanceRecordId recordId, String technicianName) {
        MaintenanceRecord record = findMaintenanceRecord(recordId);
        record.complete(technicianName);
        this.status = EquipmentStatus.IDLE;
        touch();

        registerEvent(new MaintenanceCompletedEvent(
                getId().getValue(), name, recordId.getValue(), technicianName));
    }

    /**
     * 報廢設備。
     * 這是終態，不可逆轉。
     */
    public void decommission() {
        if (this.status == EquipmentStatus.DECOMMISSIONED) {
            throw new DomainException(
                    String.format("Equipment [%s] is already decommissioned", getId().getValue()));
        }
        this.status = EquipmentStatus.DECOMMISSIONED;
        touch();
    }

    /**
     * 更新運行參數。
     */
    public void updateParameters(OperatingParameters params) {
        this.operatingParameters = Objects.requireNonNull(params, "Operating parameters must not be null");
        touch();
    }

    // ======================== 查詢方法 ========================

    private MaintenanceRecord findMaintenanceRecord(MaintenanceRecordId recordId) {
        for (MaintenanceRecord record : maintenanceRecords) {
            if (record.getId().equals(recordId)) {
                return record;
            }
        }
        throw new EntityNotFoundException("MaintenanceRecord", recordId.getValue());
    }

    // ======================== Getters ========================

    public String getName() {
        return name;
    }

    public EquipmentType getType() {
        return type;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public Location getLocation() {
        return location;
    }

    public OperatingParameters getOperatingParameters() {
        return operatingParameters;
    }

    public List<MaintenanceRecord> getMaintenanceRecords() {
        return Collections.unmodifiableList(maintenanceRecords);
    }

    // ======================== 供 Converter 重建使用 ========================
    // 這些方法設為 public 以允許基礎設施層的 Converter 重建領域物件。
    // 在生產環境中，可考慮使用 Reflection 或 Builder 模式來避免暴露 setter。

    public void setName(String name) {
        this.name = name;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setOperatingParameters(OperatingParameters operatingParameters) {
        this.operatingParameters = operatingParameters;
    }

    public void addMaintenanceRecord(MaintenanceRecord record) {
        this.maintenanceRecords.add(record);
    }

    /**
     * 供 Converter 設定建立時間（覆寫 protected 方法提升為 public）。
     */
    @Override
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        super.setCreatedAt(createdAt);
    }

    /**
     * 供 Converter 設定更新時間（覆寫 protected 方法提升為 public）。
     */
    @Override
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        super.setUpdatedAt(updatedAt);
    }
}
