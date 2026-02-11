package com.mes.boot.workorder.domain.service;

import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.common.ddd.annotation.DomainService;

import java.util.List;

/**
 * [DDD Pattern: Domain Service]
 * [SOLID: SRP - 只負責跨工單的排程衝突檢查邏輯]
 *
 * 工單領域服務。
 * 封裝不屬於任何單一工單聚合根的領域邏輯。
 *
 * 排程衝突檢查涉及多個工單之間的日期比對，
 * 這種跨聚合的邏輯不適合放在單一聚合根中，
 * 因此提取為 Domain Service。
 */
@DomainService
public class WorkOrderDomainService {

    /**
     * 檢查新工單是否與既有工單存在排程衝突。
     * 衝突定義：相同產品代碼的工單，其計畫日期範圍有重疊。
     *
     * @param newOrder       要檢查的新工單
     * @param existingOrders 既有的工單列表
     * @return 若存在排程衝突回傳 true
     */
    public boolean hasScheduleConflict(WorkOrder newOrder, List<WorkOrder> existingOrders) {
        if (existingOrders == null || existingOrders.isEmpty()) {
            return false;
        }

        String newProductCode = newOrder.getProductInfo().getProductCode();

        for (WorkOrder existing : existingOrders) {
            // 跳過自身比對
            if (existing.getId().equals(newOrder.getId())) {
                continue;
            }

            // 只檢查相同產品代碼的工單
            boolean sameProduct = existing.getProductInfo().getProductCode().equals(newProductCode);
            if (sameProduct && existing.getDateRange().overlapsWith(newOrder.getDateRange())) {
                return true;
            }
        }

        return false;
    }
}
