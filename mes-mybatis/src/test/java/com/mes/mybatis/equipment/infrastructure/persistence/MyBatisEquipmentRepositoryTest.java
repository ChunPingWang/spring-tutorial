package com.mes.mybatis.equipment.infrastructure.persistence;

import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.model.EquipmentStatus;
import com.mes.mybatis.equipment.domain.model.EquipmentType;
import com.mes.mybatis.equipment.domain.model.Location;
import com.mes.mybatis.equipment.domain.model.MaintenanceRecord;
import com.mes.mybatis.equipment.domain.model.OperatingParameters;
import com.mes.mybatis.equipment.domain.repository.EquipmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MyBatisEquipmentRepository 整合測試。
 * 驗證 Repository 正確地儲存/載入完整的聚合根（包含子 Entity）。
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("MyBatisEquipmentRepository 倉儲")
class MyBatisEquipmentRepositoryTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("應能儲存並載入設備聚合根")
    void shouldSaveAndFind() {
        // Arrange
        Equipment equipment = createTestEquipment();

        // Act
        equipmentRepository.save(equipment);
        Optional<Equipment> found = equipmentRepository.findById(equipment.getId());

        // Assert
        assertThat(found).isPresent();
        Equipment loaded = found.get();
        assertThat(loaded.getName()).isEqualTo("測試設備");
        assertThat(loaded.getType()).isEqualTo(EquipmentType.CNC);
        assertThat(loaded.getStatus()).isEqualTo(EquipmentStatus.IDLE);
        assertThat(loaded.getLocation().getBuilding()).isEqualTo("A棟");
    }

    @Test
    @DisplayName("應能儲存並載入包含維護記錄的聚合根")
    void shouldSaveAndFindWithMaintenanceRecords() {
        // Arrange
        Equipment equipment = createTestEquipment();
        equipment.scheduleMaintenance("定期保養", LocalDate.now().plusDays(7));

        // Act
        equipmentRepository.save(equipment);
        Optional<Equipment> found = equipmentRepository.findById(equipment.getId());

        // Assert
        assertThat(found).isPresent();
        Equipment loaded = found.get();
        assertThat(loaded.getMaintenanceRecords()).hasSize(1);
        MaintenanceRecord record = loaded.getMaintenanceRecords().get(0);
        assertThat(record.getDescription()).isEqualTo("定期保養");
        assertThat(record.getStatus()).isEqualTo("SCHEDULED");
    }

    @Test
    @DisplayName("應能更新設備狀態")
    void shouldUpdateEquipment() {
        // Arrange
        Equipment equipment = createTestEquipment();
        equipmentRepository.save(equipment);

        // Act
        equipment.startRunning();
        equipment.updateParameters(new OperatingParameters(50.0, 3.0, 2000.0, 0.1));
        equipmentRepository.save(equipment);

        // Assert
        Optional<Equipment> found = equipmentRepository.findById(equipment.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(EquipmentStatus.RUNNING);
        assertThat(found.get().getOperatingParameters().getTemperature()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("應能查詢所有設備")
    void shouldFindAll() {
        List<Equipment> equipments = equipmentRepository.findAll();
        // 種子資料有 5 筆
        assertThat(equipments).hasSizeGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("應能依狀態查詢設備")
    void shouldFindByStatus() {
        List<Equipment> idleEquipments = equipmentRepository.findByStatus(EquipmentStatus.IDLE);
        assertThat(idleEquipments).isNotEmpty();
        for (Equipment eq : idleEquipments) {
            assertThat(eq.getStatus()).isEqualTo(EquipmentStatus.IDLE);
        }
    }

    @Test
    @DisplayName("應能依類型查詢設備")
    void shouldFindByType() {
        List<Equipment> cncEquipments = equipmentRepository.findByType(EquipmentType.CNC);
        assertThat(cncEquipments).isNotEmpty();
        for (Equipment eq : cncEquipments) {
            assertThat(eq.getType()).isEqualTo(EquipmentType.CNC);
        }
    }

    @Test
    @DisplayName("應能刪除設備及其維護記錄")
    void shouldDeleteEquipment() {
        // Arrange
        Equipment equipment = createTestEquipment();
        equipment.scheduleMaintenance("定期保養", LocalDate.now().plusDays(7));
        equipmentRepository.save(equipment);

        // Act
        equipmentRepository.deleteById(equipment.getId());

        // Assert
        Optional<Equipment> found = equipmentRepository.findById(equipment.getId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("查詢不存在的 ID 應回傳空 Optional")
    void shouldReturnEmptyForNonExistent() {
        Optional<Equipment> found = equipmentRepository.findById(EquipmentId.of("NON-EXISTENT"));
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("儲存後再新增維護記錄應正確同步")
    void shouldSyncMaintenanceRecordsOnUpdate() {
        // Arrange
        Equipment equipment = createTestEquipment();
        equipmentRepository.save(equipment);

        // Act: 重新載入並新增維護記錄
        Equipment loaded = equipmentRepository.findById(equipment.getId()).get();
        loaded.scheduleMaintenance("第一次保養", LocalDate.now().plusDays(7));
        equipmentRepository.save(loaded);

        // Assert
        Equipment reloaded = equipmentRepository.findById(equipment.getId()).get();
        assertThat(reloaded.getMaintenanceRecords()).hasSize(1);
    }

    // ======================== 測試輔助方法 ========================

    private Equipment createTestEquipment() {
        EquipmentId id = EquipmentId.of(UUID.randomUUID().toString());
        Location location = new Location("A棟", "1", "加工區", "A1-01");
        return new Equipment(id, "測試設備", EquipmentType.CNC, location);
    }
}
