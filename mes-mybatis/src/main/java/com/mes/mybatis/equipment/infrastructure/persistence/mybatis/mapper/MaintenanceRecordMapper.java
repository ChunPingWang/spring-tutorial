package com.mes.mybatis.equipment.infrastructure.persistence.mybatis.mapper;

import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.MaintenanceRecordDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * [Infrastructure Layer: MyBatis Mapper - 維護記錄 Mapper]
 *
 * 與 EquipmentMapper 配合使用，管理維護記錄的持久化。
 * SELECT 查詢定義在 XML 中（MaintenanceRecordMapper.xml），CUD 使用註解。
 */
@Mapper
public interface MaintenanceRecordMapper {

    /**
     * 根據設備 ID 查詢所有維護記錄。
     * SQL 定義在 mybatis/mapper/MaintenanceRecordMapper.xml
     */
    List<MaintenanceRecordDO> selectByEquipmentId(@Param("equipmentId") String equipmentId);

    @Insert("INSERT INTO maintenance_record (id, equipment_id, maintenance_type, " +
            "description, scheduled_date, completed_date, technician_name, status, created_at) " +
            "VALUES (#{id}, #{equipmentId}, #{maintenanceType}, #{description}, " +
            "#{scheduledDate}, #{completedDate}, #{technicianName}, #{status}, #{createdAt})")
    void insert(MaintenanceRecordDO record);

    @Update("UPDATE maintenance_record SET maintenance_type = #{maintenanceType}, " +
            "description = #{description}, scheduled_date = #{scheduledDate}, " +
            "completed_date = #{completedDate}, technician_name = #{technicianName}, " +
            "status = #{status} WHERE id = #{id}")
    void update(MaintenanceRecordDO record);

    @Delete("DELETE FROM maintenance_record WHERE equipment_id = #{equipmentId}")
    void deleteByEquipmentId(@Param("equipmentId") String equipmentId);
}
