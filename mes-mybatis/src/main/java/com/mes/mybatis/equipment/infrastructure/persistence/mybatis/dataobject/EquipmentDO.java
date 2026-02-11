package com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * [Infrastructure Layer: Data Object - 設備資料物件]
 *
 * 扁平化的 POJO，與資料庫表 equipment 的欄位一一對應。
 * Data Object 屬於基礎設施層，不包含任何業務邏輯。
 *
 * DO (Data Object) vs Domain Model:
 * - DO 是資料庫的「鏡像」，結構由資料庫 schema 決定
 * - Domain Model 是業務概念的「表達」，結構由業務需求決定
 * - Converter 負責兩者之間的轉換
 */
@Data
public class EquipmentDO {

    private String id;
    private String name;
    private String equipmentType;
    private String status;
    private String locationBuilding;
    private String locationFloor;
    private String locationZone;
    private String locationPosition;
    private Double paramTemperature;
    private Double paramPressure;
    private Double paramSpeed;
    private Double paramVibration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
