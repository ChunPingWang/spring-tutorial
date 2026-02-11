package com.mes.mybatis.equipment.infrastructure.persistence;

import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.model.EquipmentStatus;
import com.mes.mybatis.equipment.domain.model.EquipmentType;
import com.mes.mybatis.equipment.domain.model.MaintenanceRecord;
import com.mes.mybatis.equipment.domain.repository.EquipmentRepository;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.converter.EquipmentConverter;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.EquipmentDO;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.MaintenanceRecordDO;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.mapper.EquipmentMapper;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.mapper.MaintenanceRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * [Hexagonal Architecture: Output Adapter - MyBatis 實作的設備倉儲]
 * [SOLID: DIP - 實作 Domain Layer 定義的 EquipmentRepository 介面]
 * [SOLID: LSP - 可替換為其他持久化技術的實作（如 JPA）]
 *
 * 此類別是六角形架構中的「基礎設施適配器」，負責：
 * 1. 使用 MyBatis Mapper 執行 SQL 操作
 * 2. 透過 Converter 進行 Domain Model <-> Data Object 的轉換
 * 3. 管理聚合根與其子 Entity 的持久化一致性（save 方法中同步維護記錄）
 */
@Repository
public class MyBatisEquipmentRepository implements EquipmentRepository {

    private static final Logger log = LoggerFactory.getLogger(MyBatisEquipmentRepository.class);

    private final EquipmentMapper equipmentMapper;
    private final MaintenanceRecordMapper maintenanceRecordMapper;
    private final EquipmentConverter converter;

    public MyBatisEquipmentRepository(EquipmentMapper equipmentMapper,
                                      MaintenanceRecordMapper maintenanceRecordMapper,
                                      EquipmentConverter converter) {
        this.equipmentMapper = equipmentMapper;
        this.maintenanceRecordMapper = maintenanceRecordMapper;
        this.converter = converter;
    }

    @Override
    public Optional<Equipment> findById(EquipmentId id) {
        EquipmentDO equipmentDO = equipmentMapper.selectById(id.getValue());
        if (equipmentDO == null) {
            return Optional.empty();
        }

        List<MaintenanceRecordDO> recordDOs = maintenanceRecordMapper.selectByEquipmentId(id.getValue());
        Equipment equipment = converter.toDomain(equipmentDO, recordDOs);
        return Optional.of(equipment);
    }

    @Override
    public List<Equipment> findAll() {
        List<EquipmentDO> equipmentDOs = equipmentMapper.selectAll();
        List<Equipment> equipments = new ArrayList<>();
        for (EquipmentDO equipmentDO : equipmentDOs) {
            List<MaintenanceRecordDO> recordDOs =
                    maintenanceRecordMapper.selectByEquipmentId(equipmentDO.getId());
            equipments.add(converter.toDomain(equipmentDO, recordDOs));
        }
        return equipments;
    }

    /**
     * 儲存設備聚合根。
     *
     * 採用「先判斷是否存在再決定 INSERT / UPDATE」的策略：
     * 1. 檢查設備是否已存在於資料庫
     * 2. 如果不存在則 INSERT，如果存在則 UPDATE
     * 3. 同步維護記錄：比較記憶體中的記錄與資料庫中的記錄，進行新增/更新/刪除
     *
     * 使用 @Transactional 確保聚合根及其子 Entity 的持久化是原子操作。
     */
    @Override
    @Transactional
    public void save(Equipment aggregate) {
        String equipmentId = aggregate.getId().getValue();
        EquipmentDO equipmentDO = converter.toDataObject(aggregate);

        // 判斷 INSERT 或 UPDATE
        EquipmentDO existing = equipmentMapper.selectById(equipmentId);
        if (existing == null) {
            log.info("新增設備: id={}", equipmentId);
            equipmentMapper.insert(equipmentDO);
        } else {
            log.info("更新設備: id={}", equipmentId);
            equipmentMapper.update(equipmentDO);
        }

        // 同步維護記錄
        syncMaintenanceRecords(aggregate);

        // 清除已發佈的領域事件
        aggregate.clearEvents();
    }

    @Override
    @Transactional
    public void deleteById(EquipmentId id) {
        log.info("刪除設備: id={}", id.getValue());
        maintenanceRecordMapper.deleteByEquipmentId(id.getValue());
        equipmentMapper.deleteById(id.getValue());
    }

    @Override
    public List<Equipment> findByStatus(EquipmentStatus status) {
        List<EquipmentDO> equipmentDOs = equipmentMapper.selectByStatus(status.name());
        List<Equipment> equipments = new ArrayList<>();
        for (EquipmentDO equipmentDO : equipmentDOs) {
            List<MaintenanceRecordDO> recordDOs =
                    maintenanceRecordMapper.selectByEquipmentId(equipmentDO.getId());
            equipments.add(converter.toDomain(equipmentDO, recordDOs));
        }
        return equipments;
    }

    @Override
    public List<Equipment> findByType(EquipmentType type) {
        List<EquipmentDO> equipmentDOs = equipmentMapper.selectByType(type.name());
        List<Equipment> equipments = new ArrayList<>();
        for (EquipmentDO equipmentDO : equipmentDOs) {
            List<MaintenanceRecordDO> recordDOs =
                    maintenanceRecordMapper.selectByEquipmentId(equipmentDO.getId());
            equipments.add(converter.toDomain(equipmentDO, recordDOs));
        }
        return equipments;
    }

    /**
     * 同步維護記錄：比較聚合中的記錄與資料庫中的記錄，
     * 執行必要的 INSERT / UPDATE / DELETE 操作。
     */
    private void syncMaintenanceRecords(Equipment aggregate) {
        String equipmentId = aggregate.getId().getValue();

        // 取得資料庫中現有的記錄 ID
        List<MaintenanceRecordDO> existingRecords =
                maintenanceRecordMapper.selectByEquipmentId(equipmentId);
        Set<String> existingIds = new HashSet<>();
        for (MaintenanceRecordDO record : existingRecords) {
            existingIds.add(record.getId());
        }

        // 取得聚合中的記錄 ID
        Set<String> currentIds = new HashSet<>();
        for (MaintenanceRecord record : aggregate.getMaintenanceRecords()) {
            currentIds.add(record.getId().getValue());
        }

        // INSERT 或 UPDATE 聚合中的記錄
        for (MaintenanceRecord record : aggregate.getMaintenanceRecords()) {
            MaintenanceRecordDO recordDO = converter.maintenanceRecordToDO(record, equipmentId);
            if (existingIds.contains(record.getId().getValue())) {
                maintenanceRecordMapper.update(recordDO);
            } else {
                maintenanceRecordMapper.insert(recordDO);
            }
        }

        // DELETE 不再存在的記錄（聚合中已移除的）
        for (String existingId : existingIds) {
            if (!currentIds.contains(existingId)) {
                // 此處只有在聚合從記錄列表中移除了某個記錄時才會觸發
                // 目前的設計中 MaintenanceRecord 不會被移除，但預留此邏輯
                log.debug("維護記錄已從聚合中移除: id={}", existingId);
            }
        }
    }
}
