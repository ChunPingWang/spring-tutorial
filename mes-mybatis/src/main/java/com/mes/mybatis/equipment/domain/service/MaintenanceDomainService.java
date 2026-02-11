package com.mes.mybatis.equipment.domain.service;

import com.mes.common.ddd.annotation.DomainService;
import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.MaintenanceRecord;

import java.time.LocalDate;

/**
 * [DDD Pattern: Domain Service - 維護領域服務]
 * [SOLID: SRP - 封裝不屬於單一 Entity 的跨實體/跨聚合領域邏輯]
 *
 * 此服務封裝維護相關的業務邏輯，例如判斷維護是否逾期。
 * 這類邏輯不適合放在 Equipment 聚合根中，因為它涉及日期比較等不屬於設備本身的判斷。
 */
@DomainService
public class MaintenanceDomainService {

    /**
     * 判斷設備是否有逾期未完成的維護記錄。
     *
     * 規則：如果有任何 SCHEDULED 狀態的維護記錄，其排程日期早於今天，則視為逾期。
     *
     * @param equipment 要檢查的設備
     * @return true 表示有逾期維護
     */
    public boolean isMaintenanceOverdue(Equipment equipment) {
        LocalDate today = LocalDate.now();
        for (MaintenanceRecord record : equipment.getMaintenanceRecords()) {
            if ("SCHEDULED".equals(record.getStatus())
                    && record.getScheduledDate().isBefore(today)) {
                return true;
            }
        }
        return false;
    }
}
