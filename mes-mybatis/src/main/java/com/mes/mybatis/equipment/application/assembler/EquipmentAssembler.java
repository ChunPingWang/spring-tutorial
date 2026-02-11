package com.mes.mybatis.equipment.application.assembler;

import com.mes.mybatis.equipment.application.query.dto.EquipmentDetailView;
import com.mes.mybatis.equipment.application.query.dto.MaintenanceHistoryView;
import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.MaintenanceRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * [Application Layer: Assembler - 設備組裝器]
 * [SOLID: SRP - 專責 Domain Model 與 DTO 之間的轉換]
 *
 * Assembler 負責將 Domain Model 轉換為查詢用 DTO（View）。
 * 與 Converter（基礎設施層）不同：
 * - Assembler: Domain Model <-> Application DTO
 * - Converter: Domain Model <-> Data Object (持久化)
 */
@Component
public class EquipmentAssembler {

    /**
     * 將 Equipment 聚合根轉換為詳情視圖。
     */
    public EquipmentDetailView toDetailView(Equipment equipment) {
        EquipmentDetailView view = new EquipmentDetailView();
        view.setId(equipment.getId().getValue());
        view.setName(equipment.getName());
        view.setType(equipment.getType().name());
        view.setStatus(equipment.getStatus().name());
        view.setBuilding(equipment.getLocation().getBuilding());
        view.setFloor(equipment.getLocation().getFloor());
        view.setZone(equipment.getLocation().getZone());
        view.setPosition(equipment.getLocation().getPosition());
        view.setTemperature(equipment.getOperatingParameters().getTemperature());
        view.setPressure(equipment.getOperatingParameters().getPressure());
        view.setSpeed(equipment.getOperatingParameters().getSpeed());
        view.setVibration(equipment.getOperatingParameters().getVibration());
        view.setCreatedAt(equipment.getCreatedAt());
        view.setUpdatedAt(equipment.getUpdatedAt());

        List<MaintenanceHistoryView> maintenanceViews = new ArrayList<>();
        for (MaintenanceRecord record : equipment.getMaintenanceRecords()) {
            maintenanceViews.add(toMaintenanceView(record));
        }
        view.setMaintenanceRecords(maintenanceViews);

        return view;
    }

    /**
     * 將 MaintenanceRecord Entity 轉換為維護歷史視圖。
     */
    public MaintenanceHistoryView toMaintenanceView(MaintenanceRecord record) {
        MaintenanceHistoryView view = new MaintenanceHistoryView();
        view.setId(record.getId().getValue());
        view.setEquipmentId(record.getEquipmentId());
        view.setMaintenanceType(record.getMaintenanceType());
        view.setDescription(record.getDescription());
        view.setScheduledDate(record.getScheduledDate());
        view.setCompletedDate(record.getCompletedDate());
        view.setTechnicianName(record.getTechnicianName());
        view.setStatus(record.getStatus());
        view.setCreatedAt(record.getCreatedAt());
        return view;
    }
}
