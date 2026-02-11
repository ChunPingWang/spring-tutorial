package com.mes.boot.workorder.domain.repository;

import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.boot.workorder.domain.model.WorkOrderId;
import com.mes.boot.workorder.domain.model.WorkOrderStatus;
import com.mes.common.ddd.repository.Repository;

import java.util.List;

/**
 * [DDD Pattern: Repository - Port (出站埠)]
 * [SOLID: ISP - 只定義工單聚合根需要的持久化操作]
 * [SOLID: DIP - 領域層定義介面，基礎設施層提供具體實作]
 * [Hexagonal Architecture: Output Port]
 *
 * 工單 Repository 介面，定義在領域層中。
 * 繼承通用的 Repository 介面，並新增工單特有的查詢方法。
 *
 * 實作將由基礎設施層提供（如 InMemoryWorkOrderRepository、JpaWorkOrderRepository）。
 */
public interface WorkOrderRepository extends Repository<WorkOrder, WorkOrderId> {

    /**
     * 根據工單狀態查詢工單列表。
     *
     * @param status 工單狀態
     * @return 符合條件的工單列表
     */
    List<WorkOrder> findByStatus(WorkOrderStatus status);

    /**
     * 根據產品代碼查詢工單列表。
     *
     * @param productCode 產品代碼
     * @return 符合條件的工單列表
     */
    List<WorkOrder> findByProductCode(String productCode);
}
