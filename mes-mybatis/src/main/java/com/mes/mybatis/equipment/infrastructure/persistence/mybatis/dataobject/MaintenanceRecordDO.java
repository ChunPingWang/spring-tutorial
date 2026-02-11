package com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [Infrastructure Layer: Data Object - 維護記錄資料物件]
 *
 * 扁平化的 POJO，與資料庫表 maintenance_record 的欄位一一對應。
 */
@Data
public class MaintenanceRecordDO {

    private String id;
    private String equipmentId;
    private String maintenanceType;
    private String description;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private String technicianName;
    private String status;
    private LocalDateTime createdAt;
}
