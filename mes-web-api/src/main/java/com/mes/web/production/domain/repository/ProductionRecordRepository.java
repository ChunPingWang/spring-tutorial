package com.mes.web.production.domain.repository;

import com.mes.common.ddd.repository.Repository;
import com.mes.web.production.domain.model.ProductionLineId;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionRecordId;
import com.mes.web.production.domain.model.ProductionStatus;

import java.util.List;

/**
 * [DDD Pattern: Repository - Port (出站埠)]
 * [SOLID: ISP - 定義生產紀錄聚合根專屬的查詢方法]
 * [SOLID: DIP - 領域層定義介面，基礎設施層實作]
 * [Hexagonal Architecture: Output Port]
 *
 * 生產紀錄的 Repository 介面，擴展通用 Repository 並新增特定查詢方法。
 * 此介面定義在領域層，實作放在基礎設施層，實現依賴反轉。
 */
public interface ProductionRecordRepository
        extends Repository<ProductionRecord, ProductionRecordId> {

    /**
     * 依產線 ID 查找所有生產紀錄。
     *
     * @param lineId 產線 ID
     * @return 該產線的所有生產紀錄
     */
    List<ProductionRecord> findByLineId(ProductionLineId lineId);

    /**
     * 依狀態查找所有生產紀錄。
     *
     * @param status 生產狀態
     * @return 該狀態的所有生產紀錄
     */
    List<ProductionRecord> findByStatus(ProductionStatus status);

    /**
     * 依工單 ID 查找所有生產紀錄。
     *
     * @param workOrderId 工單 ID
     * @return 該工單的所有生產紀錄
     */
    List<ProductionRecord> findByWorkOrderId(String workOrderId);
}
