package com.mes.mybatis.equipment.domain.repository;

import com.mes.common.ddd.repository.Repository;
import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.model.EquipmentStatus;
import com.mes.mybatis.equipment.domain.model.EquipmentType;

import java.util.List;

/**
 * [DDD Pattern: Repository - 設備倉儲 (Output Port)]
 * [Hexagonal Architecture: 這是領域層定義的輸出埠，由基礎設施層實作]
 * [SOLID: DIP - 領域層定義抽象介面，不依賴具體的持久化技術]
 *
 * 除了基本的 CRUD 操作外，提供依狀態和類型查詢的方法。
 */
public interface EquipmentRepository extends Repository<Equipment, EquipmentId> {

    /**
     * 根據設備狀態查詢設備列表。
     *
     * @param status 設備狀態
     * @return 符合條件的設備列表
     */
    List<Equipment> findByStatus(EquipmentStatus status);

    /**
     * 根據設備類型查詢設備列表。
     *
     * @param type 設備類型
     * @return 符合條件的設備列表
     */
    List<Equipment> findByType(EquipmentType type);
}
