package com.mes.mybatis.equipment.infrastructure.persistence.mybatis.mapper;

import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.EquipmentDO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EquipmentMapper 整合測試。
 * 使用 @SpringBootTest + H2 記憶體資料庫驗證 MyBatis SQL 的正確性。
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("EquipmentMapper MyBatis 映射")
class EquipmentMapperTest {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Test
    @DisplayName("應能根據 ID 查詢設備（使用種子資料）")
    void shouldSelectById() {
        EquipmentDO equipment = equipmentMapper.selectById("EQ-001");

        assertThat(equipment).isNotNull();
        assertThat(equipment.getName()).isEqualTo("CNC 加工中心 A1");
        assertThat(equipment.getEquipmentType()).isEqualTo("CNC");
        assertThat(equipment.getStatus()).isEqualTo("RUNNING");
        assertThat(equipment.getLocationBuilding()).isEqualTo("A棟");
    }

    @Test
    @DisplayName("查詢不存在的 ID 應回傳 null")
    void shouldReturnNullForNonExistentId() {
        EquipmentDO equipment = equipmentMapper.selectById("NON-EXISTENT");
        assertThat(equipment).isNull();
    }

    @Test
    @DisplayName("應能查詢所有設備")
    void shouldSelectAll() {
        List<EquipmentDO> equipments = equipmentMapper.selectAll();
        assertThat(equipments).hasSizeGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("應能依狀態查詢設備")
    void shouldSelectByStatus() {
        List<EquipmentDO> runningEquipments = equipmentMapper.selectByStatus("RUNNING");
        assertThat(runningEquipments).isNotEmpty();
        for (EquipmentDO eq : runningEquipments) {
            assertThat(eq.getStatus()).isEqualTo("RUNNING");
        }
    }

    @Test
    @DisplayName("應能依類型查詢設備")
    void shouldSelectByType() {
        List<EquipmentDO> cncEquipments = equipmentMapper.selectByType("CNC");
        assertThat(cncEquipments).isNotEmpty();
        for (EquipmentDO eq : cncEquipments) {
            assertThat(eq.getEquipmentType()).isEqualTo("CNC");
        }
    }

    @Test
    @DisplayName("應能新增設備")
    void shouldInsertEquipment() {
        String id = UUID.randomUUID().toString();
        EquipmentDO newEquipment = createEquipmentDO(id, "新設備", "ROBOT", "IDLE");

        equipmentMapper.insert(newEquipment);

        EquipmentDO retrieved = equipmentMapper.selectById(id);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("新設備");
        assertThat(retrieved.getEquipmentType()).isEqualTo("ROBOT");
    }

    @Test
    @DisplayName("應能更新設備")
    void shouldUpdateEquipment() {
        // 先新增
        String id = UUID.randomUUID().toString();
        EquipmentDO equipment = createEquipmentDO(id, "原始名稱", "CNC", "IDLE");
        equipmentMapper.insert(equipment);

        // 更新
        equipment.setName("更新後名稱");
        equipment.setStatus("RUNNING");
        equipment.setUpdatedAt(LocalDateTime.now());
        equipmentMapper.update(equipment);

        // 驗證
        EquipmentDO updated = equipmentMapper.selectById(id);
        assertThat(updated.getName()).isEqualTo("更新後名稱");
        assertThat(updated.getStatus()).isEqualTo("RUNNING");
    }

    @Test
    @DisplayName("應能刪除設備")
    void shouldDeleteEquipment() {
        String id = UUID.randomUUID().toString();
        EquipmentDO equipment = createEquipmentDO(id, "待刪除", "CONVEYOR", "IDLE");
        equipmentMapper.insert(equipment);

        equipmentMapper.deleteById(id);

        EquipmentDO deleted = equipmentMapper.selectById(id);
        assertThat(deleted).isNull();
    }

    // ======================== 測試輔助方法 ========================

    private EquipmentDO createEquipmentDO(String id, String name, String type, String status) {
        EquipmentDO equipment = new EquipmentDO();
        equipment.setId(id);
        equipment.setName(name);
        equipment.setEquipmentType(type);
        equipment.setStatus(status);
        equipment.setLocationBuilding("Test棟");
        equipment.setLocationFloor("1");
        equipment.setLocationZone("測試區");
        equipment.setLocationPosition("T1-01");
        equipment.setParamTemperature(25.0);
        equipment.setParamPressure(1.0);
        equipment.setParamSpeed(100.0);
        equipment.setParamVibration(0.01);
        equipment.setCreatedAt(LocalDateTime.now());
        equipment.setUpdatedAt(LocalDateTime.now());
        return equipment;
    }
}
