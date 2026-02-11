package com.mes.kafka.quality.domain.repository;

import com.mes.common.ddd.repository.Repository;
import com.mes.kafka.quality.domain.model.InspectionOrder;
import com.mes.kafka.quality.domain.model.InspectionOrderId;
import com.mes.kafka.quality.domain.model.InspectionStatus;

import java.util.List;

/**
 * [DDD Pattern: Repository - Port (出站埠)]
 * [SOLID: ISP - 只定義檢驗工單所需的查詢操作]
 * [SOLID: DIP - 領域層定義介面，基礎設施層提供實作]
 * [Hexagonal Architecture: Output Port]
 *
 * 檢驗工單儲存庫介面。
 * 在領域層中定義，由基礎設施層實作（如 InMemoryInspectionOrderRepository）。
 * 擴展基礎 Repository 介面，增加品質檢驗領域特定的查詢方法。
 */
public interface InspectionOrderRepository extends Repository<InspectionOrder, InspectionOrderId> {

    /**
     * 根據工單 ID 查找檢驗工單。
     *
     * @param workOrderId 工單 ID
     * @return 符合條件的檢驗工單列表
     */
    List<InspectionOrder> findByWorkOrderId(String workOrderId);

    /**
     * 根據檢驗狀態查找檢驗工單。
     *
     * @param status 檢驗狀態
     * @return 符合條件的檢驗工單列表
     */
    List<InspectionOrder> findByStatus(InspectionStatus status);
}
