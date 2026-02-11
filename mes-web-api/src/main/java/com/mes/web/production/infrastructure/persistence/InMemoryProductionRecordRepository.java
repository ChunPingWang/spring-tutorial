package com.mes.web.production.infrastructure.persistence;

import com.mes.web.production.domain.model.ProductionLineId;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionRecordId;
import com.mes.web.production.domain.model.ProductionStatus;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [DDD Pattern: Repository - Adapter (基礎設施層實作)]
 * [SOLID: DIP - 實作領域層定義的 Repository 介面]
 * [SOLID: LSP - 可替換為 JPA/MyBatis 等其他實作]
 * [Hexagonal Architecture: Output Adapter]
 *
 * 使用 ConcurrentHashMap 的記憶體實作。
 * 適用於開發、測試和教學環境，不適用於生產環境。
 *
 * 此實作展示了六角形架構的核心概念：
 * 領域層定義介面（Port），基礎設施層提供實作（Adapter）。
 * 未來可輕鬆替換為 JPA 或 MyBatis 實作而不影響領域邏輯。
 */
@Component
public class InMemoryProductionRecordRepository implements ProductionRecordRepository {

    private final ConcurrentHashMap<ProductionRecordId, ProductionRecord> store =
            new ConcurrentHashMap<ProductionRecordId, ProductionRecord>();

    @Override
    public Optional<ProductionRecord> findById(ProductionRecordId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ProductionRecord> findAll() {
        return new ArrayList<ProductionRecord>(store.values());
    }

    @Override
    public void save(ProductionRecord aggregate) {
        store.put(aggregate.getId(), aggregate);
    }

    @Override
    public void deleteById(ProductionRecordId id) {
        store.remove(id);
    }

    @Override
    public List<ProductionRecord> findByLineId(ProductionLineId lineId) {
        List<ProductionRecord> result = new ArrayList<ProductionRecord>();
        for (ProductionRecord record : store.values()) {
            if (record.getProductionLine().getLineId().equals(lineId)) {
                result.add(record);
            }
        }
        return result;
    }

    @Override
    public List<ProductionRecord> findByStatus(ProductionStatus status) {
        List<ProductionRecord> result = new ArrayList<ProductionRecord>();
        for (ProductionRecord record : store.values()) {
            if (record.getStatus() == status) {
                result.add(record);
            }
        }
        return result;
    }

    @Override
    public List<ProductionRecord> findByWorkOrderId(String workOrderId) {
        List<ProductionRecord> result = new ArrayList<ProductionRecord>();
        for (ProductionRecord record : store.values()) {
            if (record.getWorkOrderId().equals(workOrderId)) {
                result.add(record);
            }
        }
        return result;
    }
}
