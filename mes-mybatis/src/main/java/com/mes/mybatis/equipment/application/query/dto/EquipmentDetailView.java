package com.mes.mybatis.equipment.application.query.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * [CQRS Pattern: Read Model DTO - 設備詳情視圖]
 *
 * 包含設備的完整資訊，用於查詢結果的展示。
 * 這是 CQRS 讀取端的資料結構，與寫入端的 Domain Model 解耦。
 */
public class EquipmentDetailView {

    private String id;
    private String name;
    private String type;
    private String status;
    private String building;
    private String floor;
    private String zone;
    private String position;
    private double temperature;
    private double pressure;
    private double speed;
    private double vibration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MaintenanceHistoryView> maintenanceRecords;

    public EquipmentDetailView() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getVibration() {
        return vibration;
    }

    public void setVibration(double vibration) {
        this.vibration = vibration;
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

    public List<MaintenanceHistoryView> getMaintenanceRecords() {
        return maintenanceRecords;
    }

    public void setMaintenanceRecords(List<MaintenanceHistoryView> maintenanceRecords) {
        this.maintenanceRecords = maintenanceRecords;
    }
}
