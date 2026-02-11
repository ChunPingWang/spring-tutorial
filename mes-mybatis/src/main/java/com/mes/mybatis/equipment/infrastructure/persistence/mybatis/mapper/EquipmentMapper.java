package com.mes.mybatis.equipment.infrastructure.persistence.mybatis.mapper;

import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.EquipmentDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * [Infrastructure Layer: MyBatis Mapper - 設備 Mapper]
 * [Hexagonal Architecture: Output Adapter - 實作持久化的技術細節]
 *
 * MyBatis Mapper 混合使用兩種 SQL 定義方式：
 * - 註解方式（@Insert, @Update, @Delete）：適合簡單的 SQL
 * - XML 方式（EquipmentMapper.xml）：適合複雜的查詢（如 resultMap、動態 SQL）
 *
 * 簡單的 INSERT/UPDATE/DELETE 使用註解，
 * 複雜的 SELECT（需要欄位映射）使用 XML。
 */
@Mapper
public interface EquipmentMapper {

    // ---- XML-mapped 查詢（複雜查詢放在 EquipmentMapper.xml 中） ----

    /**
     * 根據 ID 查詢設備。
     * SQL 定義在 mybatis/mapper/EquipmentMapper.xml
     */
    EquipmentDO selectById(@Param("id") String id);

    /**
     * 查詢所有設備。
     */
    List<EquipmentDO> selectAll();

    /**
     * 依狀態查詢設備。
     */
    List<EquipmentDO> selectByStatus(@Param("status") String status);

    /**
     * 依類型查詢設備。
     */
    List<EquipmentDO> selectByType(@Param("equipmentType") String equipmentType);

    // ---- 註解方式（簡單的 CUD 操作） ----

    @Insert("INSERT INTO equipment (id, name, equipment_type, status, " +
            "location_building, location_floor, location_zone, location_position, " +
            "param_temperature, param_pressure, param_speed, param_vibration, " +
            "created_at, updated_at) VALUES (" +
            "#{id}, #{name}, #{equipmentType}, #{status}, " +
            "#{locationBuilding}, #{locationFloor}, #{locationZone}, #{locationPosition}, " +
            "#{paramTemperature}, #{paramPressure}, #{paramSpeed}, #{paramVibration}, " +
            "#{createdAt}, #{updatedAt})")
    void insert(EquipmentDO equipmentDO);

    @Update("UPDATE equipment SET name = #{name}, equipment_type = #{equipmentType}, " +
            "status = #{status}, location_building = #{locationBuilding}, " +
            "location_floor = #{locationFloor}, location_zone = #{locationZone}, " +
            "location_position = #{locationPosition}, param_temperature = #{paramTemperature}, " +
            "param_pressure = #{paramPressure}, param_speed = #{paramSpeed}, " +
            "param_vibration = #{paramVibration}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(EquipmentDO equipmentDO);

    @Delete("DELETE FROM equipment WHERE id = #{id}")
    void deleteById(@Param("id") String id);
}
