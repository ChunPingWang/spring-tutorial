package com.mes.mybatis.equipment.infrastructure.persistence.mybatis.converter;

import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.model.EquipmentStatus;
import com.mes.mybatis.equipment.domain.model.EquipmentType;
import com.mes.mybatis.equipment.domain.model.Location;
import com.mes.mybatis.equipment.domain.model.MaintenanceRecord;
import com.mes.mybatis.equipment.domain.model.MaintenanceRecordId;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.EquipmentDO;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.MaintenanceRecordDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EquipmentConverter 單元測試。
 * 驗證 Domain Model <-> Data Object 的雙向轉換正確性。
 */
@DisplayName("EquipmentConverter 轉換器")
class EquipmentConverterTest {

    private EquipmentConverter converter;

    @BeforeEach
    void setUp() {
        converter = new EquipmentConverter();
    }

    @Nested
    @DisplayName("toDomain - DO 轉換為 Domain Model")
    class ToDomain {

        @Test
        @DisplayName("應正確轉換基本屬性")
        void shouldConvertBasicProperties() {
            EquipmentDO equipmentDO = createEquipmentDO();
            Equipment equipment = converter.toDomain(equipmentDO, Collections.<MaintenanceRecordDO>emptyList());

            assertThat(equipment.getId().getValue()).isEqualTo("EQ-001");
            assertThat(equipment.getName()).isEqualTo("CNC 加工中心");
            assertThat(equipment.getType()).isEqualTo(EquipmentType.CNC);
            assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.RUNNING);
        }

        @Test
        @DisplayName("應正確重建 Location Value Object")
        void shouldReconstructLocation() {
            EquipmentDO equipmentDO = createEquipmentDO();
            Equipment equipment = converter.toDomain(equipmentDO, Collections.<MaintenanceRecordDO>emptyList());

            Location location = equipment.getLocation();
            assertThat(location.getBuilding()).isEqualTo("A棟");
            assertThat(location.getFloor()).isEqualTo("1");
            assertThat(location.getZone()).isEqualTo("加工區");
            assertThat(location.getPosition()).isEqualTo("A1-01");
        }

        @Test
        @DisplayName("應正確重建 OperatingParameters Value Object")
        void shouldReconstructParameters() {
            EquipmentDO equipmentDO = createEquipmentDO();
            Equipment equipment = converter.toDomain(equipmentDO, Collections.<MaintenanceRecordDO>emptyList());

            assertThat(equipment.getOperatingParameters().getTemperature()).isEqualTo(45.5);
            assertThat(equipment.getOperatingParameters().getPressure()).isEqualTo(2.1);
            assertThat(equipment.getOperatingParameters().getSpeed()).isEqualTo(3000.0);
            assertThat(equipment.getOperatingParameters().getVibration()).isEqualTo(0.05);
        }

        @Test
        @DisplayName("應正確載入維護記錄")
        void shouldLoadMaintenanceRecords() {
            EquipmentDO equipmentDO = createEquipmentDO();
            List<MaintenanceRecordDO> recordDOs = Arrays.asList(createMaintenanceRecordDO());
            Equipment equipment = converter.toDomain(equipmentDO, recordDOs);

            assertThat(equipment.getMaintenanceRecords()).hasSize(1);
            MaintenanceRecord record = equipment.getMaintenanceRecords().get(0);
            assertThat(record.getId().getValue()).isEqualTo("MR-001");
            assertThat(record.getDescription()).isEqualTo("主軸定期保養");
        }

        @Test
        @DisplayName("null 輸入應回傳 null")
        void shouldReturnNullForNullInput() {
            Equipment equipment = converter.toDomain(null, Collections.<MaintenanceRecordDO>emptyList());
            assertThat(equipment).isNull();
        }
    }

    @Nested
    @DisplayName("toDataObject - Domain Model 轉換為 DO")
    class ToDataObject {

        @Test
        @DisplayName("應正確展平所有屬性")
        void shouldFlattenAllProperties() {
            Equipment equipment = createEquipment();
            EquipmentDO dataObject = converter.toDataObject(equipment);

            assertThat(dataObject.getId()).isEqualTo("EQ-001");
            assertThat(dataObject.getName()).isEqualTo("CNC 加工中心");
            assertThat(dataObject.getEquipmentType()).isEqualTo("CNC");
            assertThat(dataObject.getStatus()).isEqualTo("IDLE");
            assertThat(dataObject.getLocationBuilding()).isEqualTo("A棟");
            assertThat(dataObject.getLocationFloor()).isEqualTo("1");
            assertThat(dataObject.getLocationZone()).isEqualTo("加工區");
            assertThat(dataObject.getLocationPosition()).isEqualTo("A1-01");
        }

        @Test
        @DisplayName("null 輸入應回傳 null")
        void shouldReturnNullForNullInput() {
            EquipmentDO dataObject = converter.toDataObject(null);
            assertThat(dataObject).isNull();
        }
    }

    @Nested
    @DisplayName("maintenanceRecordToDO - 維護記錄轉換")
    class MaintenanceRecordToDO {

        @Test
        @DisplayName("應正確轉換維護記錄")
        void shouldConvertMaintenanceRecord() {
            MaintenanceRecordId recordId = MaintenanceRecordId.of("MR-001");
            MaintenanceRecord record = new MaintenanceRecord(
                    recordId, "EQ-001", "PREVENTIVE", "定期保養", LocalDate.of(2025, 3, 1));

            MaintenanceRecordDO dataObject = converter.maintenanceRecordToDO(record, "EQ-001");

            assertThat(dataObject.getId()).isEqualTo("MR-001");
            assertThat(dataObject.getEquipmentId()).isEqualTo("EQ-001");
            assertThat(dataObject.getMaintenanceType()).isEqualTo("PREVENTIVE");
            assertThat(dataObject.getDescription()).isEqualTo("定期保養");
            assertThat(dataObject.getStatus()).isEqualTo("SCHEDULED");
        }
    }

    @Nested
    @DisplayName("往返轉換 (Round-Trip)")
    class RoundTrip {

        @Test
        @DisplayName("Domain -> DO -> Domain 應保持資料一致")
        void shouldMaintainDataConsistency() {
            Equipment original = createEquipment();
            EquipmentDO dataObject = converter.toDataObject(original);
            Equipment restored = converter.toDomain(dataObject, Collections.<MaintenanceRecordDO>emptyList());

            assertThat(restored.getId()).isEqualTo(original.getId());
            assertThat(restored.getName()).isEqualTo(original.getName());
            assertThat(restored.getType()).isEqualTo(original.getType());
            assertThat(restored.getLocation()).isEqualTo(original.getLocation());
            assertThat(restored.getOperatingParameters()).isEqualTo(original.getOperatingParameters());
        }
    }

    // ======================== 測試輔助方法 ========================

    private EquipmentDO createEquipmentDO() {
        EquipmentDO equipmentDO = new EquipmentDO();
        equipmentDO.setId("EQ-001");
        equipmentDO.setName("CNC 加工中心");
        equipmentDO.setEquipmentType("CNC");
        equipmentDO.setStatus("RUNNING");
        equipmentDO.setLocationBuilding("A棟");
        equipmentDO.setLocationFloor("1");
        equipmentDO.setLocationZone("加工區");
        equipmentDO.setLocationPosition("A1-01");
        equipmentDO.setParamTemperature(45.5);
        equipmentDO.setParamPressure(2.1);
        equipmentDO.setParamSpeed(3000.0);
        equipmentDO.setParamVibration(0.05);
        equipmentDO.setCreatedAt(LocalDateTime.now());
        equipmentDO.setUpdatedAt(LocalDateTime.now());
        return equipmentDO;
    }

    private MaintenanceRecordDO createMaintenanceRecordDO() {
        MaintenanceRecordDO recordDO = new MaintenanceRecordDO();
        recordDO.setId("MR-001");
        recordDO.setEquipmentId("EQ-001");
        recordDO.setMaintenanceType("PREVENTIVE");
        recordDO.setDescription("主軸定期保養");
        recordDO.setScheduledDate(LocalDate.of(2025, 1, 15));
        recordDO.setCompletedDate(LocalDate.of(2025, 1, 15));
        recordDO.setTechnicianName("張技師");
        recordDO.setStatus("COMPLETED");
        recordDO.setCreatedAt(LocalDateTime.now());
        return recordDO;
    }

    private Equipment createEquipment() {
        EquipmentId id = EquipmentId.of("EQ-001");
        Location location = new Location("A棟", "1", "加工區", "A1-01");
        return new Equipment(id, "CNC 加工中心", EquipmentType.CNC, location);
    }
}
