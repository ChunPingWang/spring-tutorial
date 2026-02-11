package com.mes.mybatis.equipment.infrastructure.persistence.mybatis.converter;

import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.model.EquipmentStatus;
import com.mes.mybatis.equipment.domain.model.EquipmentType;
import com.mes.mybatis.equipment.domain.model.Location;
import com.mes.mybatis.equipment.domain.model.MaintenanceRecord;
import com.mes.mybatis.equipment.domain.model.MaintenanceRecordId;
import com.mes.mybatis.equipment.domain.model.OperatingParameters;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.EquipmentDO;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.MaintenanceRecordDO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * [Infrastructure Layer: Converter - 設備領域物件轉換器]
 * [Hexagonal Architecture: Anti-Corruption Layer 的一部分]
 *
 * 負責 Domain Model 與 Data Object 之間的雙向轉換。
 * 這是六角形架構中 Adapter 層的核心職責之一：
 * - toDomain: 將資料庫的扁平結構「還原」為富含業務意義的領域物件
 * - toDataObject: 將領域物件「展平」為適合持久化的資料結構
 *
 * 注意：此轉換器使用手工編寫而非 MapStruct，
 * 以便清楚展示 DO 與 Domain Model 之間的結構差異。
 */
@Component
public class EquipmentConverter {

    /**
     * 將 EquipmentDO 及其關聯的 MaintenanceRecordDO 轉換為 Equipment 聚合根。
     *
     * 重建過程：
     * 1. 從 DO 的扁平欄位重建 Value Object（EquipmentId, Location, OperatingParameters）
     * 2. 組裝 Equipment 聚合根
     * 3. 載入關聯的 MaintenanceRecord Entity
     *
     * @param equipmentDO 設備資料物件
     * @param recordDOs   維護記錄資料物件列表
     * @return 完整的 Equipment 聚合根
     */
    public Equipment toDomain(EquipmentDO equipmentDO, List<MaintenanceRecordDO> recordDOs) {
        if (equipmentDO == null) {
            return null;
        }

        // 重建 Value Objects
        EquipmentId id = EquipmentId.of(equipmentDO.getId());
        Location location = new Location(
                equipmentDO.getLocationBuilding(),
                equipmentDO.getLocationFloor(),
                equipmentDO.getLocationZone(),
                equipmentDO.getLocationPosition());
        OperatingParameters params = new OperatingParameters(
                equipmentDO.getParamTemperature() != null ? equipmentDO.getParamTemperature() : 0.0,
                equipmentDO.getParamPressure() != null ? equipmentDO.getParamPressure() : 0.0,
                equipmentDO.getParamSpeed() != null ? equipmentDO.getParamSpeed() : 0.0,
                equipmentDO.getParamVibration() != null ? equipmentDO.getParamVibration() : 0.0);

        // 使用包級私有 setter 重建 Equipment（繞過業務規則檢查）
        Equipment equipment = new Equipment(id, equipmentDO.getName(),
                EquipmentType.valueOf(equipmentDO.getEquipmentType()), location);
        equipment.setStatus(EquipmentStatus.valueOf(equipmentDO.getStatus()));
        equipment.setOperatingParameters(params);
        equipment.setCreatedAt(equipmentDO.getCreatedAt());
        equipment.setUpdatedAt(equipmentDO.getUpdatedAt());

        // 重建 MaintenanceRecord Entities
        if (recordDOs != null) {
            for (MaintenanceRecordDO recordDO : recordDOs) {
                equipment.addMaintenanceRecord(maintenanceRecordToDomain(recordDO));
            }
        }

        return equipment;
    }

    /**
     * 將 Equipment 聚合根轉換為 EquipmentDO。
     *
     * 展平過程：將巢狀的 Value Object 展開為扁平欄位。
     *
     * @param equipment 設備聚合根
     * @return 設備資料物件
     */
    public EquipmentDO toDataObject(Equipment equipment) {
        if (equipment == null) {
            return null;
        }

        EquipmentDO dataObject = new EquipmentDO();
        dataObject.setId(equipment.getId().getValue());
        dataObject.setName(equipment.getName());
        dataObject.setEquipmentType(equipment.getType().name());
        dataObject.setStatus(equipment.getStatus().name());
        dataObject.setLocationBuilding(equipment.getLocation().getBuilding());
        dataObject.setLocationFloor(equipment.getLocation().getFloor());
        dataObject.setLocationZone(equipment.getLocation().getZone());
        dataObject.setLocationPosition(equipment.getLocation().getPosition());
        dataObject.setParamTemperature(equipment.getOperatingParameters().getTemperature());
        dataObject.setParamPressure(equipment.getOperatingParameters().getPressure());
        dataObject.setParamSpeed(equipment.getOperatingParameters().getSpeed());
        dataObject.setParamVibration(equipment.getOperatingParameters().getVibration());
        dataObject.setCreatedAt(equipment.getCreatedAt());
        dataObject.setUpdatedAt(equipment.getUpdatedAt());

        return dataObject;
    }

    /**
     * 將 MaintenanceRecord Entity 轉換為 MaintenanceRecordDO。
     *
     * @param record      維護記錄
     * @param equipmentId 所屬設備 ID
     * @return 維護記錄資料物件
     */
    public MaintenanceRecordDO maintenanceRecordToDO(MaintenanceRecord record, String equipmentId) {
        if (record == null) {
            return null;
        }

        MaintenanceRecordDO dataObject = new MaintenanceRecordDO();
        dataObject.setId(record.getId().getValue());
        dataObject.setEquipmentId(equipmentId);
        dataObject.setMaintenanceType(record.getMaintenanceType());
        dataObject.setDescription(record.getDescription());
        dataObject.setScheduledDate(record.getScheduledDate());
        dataObject.setCompletedDate(record.getCompletedDate());
        dataObject.setTechnicianName(record.getTechnicianName());
        dataObject.setStatus(record.getStatus());
        dataObject.setCreatedAt(record.getCreatedAt());

        return dataObject;
    }

    /**
     * 將 MaintenanceRecordDO 轉換為 MaintenanceRecord Entity。
     */
    private MaintenanceRecord maintenanceRecordToDomain(MaintenanceRecordDO recordDO) {
        MaintenanceRecordId id = MaintenanceRecordId.of(recordDO.getId());
        MaintenanceRecord record = new MaintenanceRecord(
                id, recordDO.getEquipmentId(), recordDO.getMaintenanceType(),
                recordDO.getDescription(), recordDO.getScheduledDate());
        record.setCompletedDate(recordDO.getCompletedDate());
        record.setTechnicianName(recordDO.getTechnicianName());
        record.setStatus(recordDO.getStatus());
        record.setCreatedAt(recordDO.getCreatedAt());
        return record;
    }
}
