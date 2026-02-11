package com.mes.boot.workorder.infrastructure.persistence;

import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.boot.workorder.domain.model.WorkOrderId;
import com.mes.boot.workorder.domain.model.WorkOrderStatus;
import com.mes.boot.workorder.domain.repository.WorkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [DDD Pattern: Repository Implementation - Adapter (出站適配器)]
 * [SOLID: LSP - 完全實作 WorkOrderRepository 介面的契約]
 * [SOLID: DIP - 實作領域層定義的 Repository 介面]
 * [Hexagonal Architecture: Output Adapter - 實作 Output Port]
 *
 * 基於記憶體的工單 Repository 實作。
 * 使用 {@link ConcurrentHashMap} 確保執行緒安全。
 *
 * 適用於：
 * <ul>
 *   <li>Module 1 的學習與原型驗證</li>
 *   <li>單元測試中的快速驗證</li>
 * </ul>
 *
 * 在後續模組中將被替換為 JPA 或 MyBatis 的實作。
 */
@Component
public class InMemoryWorkOrderRepository implements WorkOrderRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryWorkOrderRepository.class);

    private final Map<WorkOrderId, WorkOrder> store = new ConcurrentHashMap<>();

    @Override
    public Optional<WorkOrder> findById(WorkOrderId id) {
        log.debug("Finding work order by id: {}", id);
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<WorkOrder> findAll() {
        log.debug("Finding all work orders, count: {}", store.size());
        return new ArrayList<>(store.values());
    }

    @Override
    public void save(WorkOrder aggregate) {
        log.debug("Saving work order: {}", aggregate.getId());
        store.put(aggregate.getId(), aggregate);
    }

    @Override
    public void deleteById(WorkOrderId id) {
        log.debug("Deleting work order: {}", id);
        store.remove(id);
    }

    @Override
    public List<WorkOrder> findByStatus(WorkOrderStatus status) {
        log.debug("Finding work orders by status: {}", status);
        List<WorkOrder> result = new ArrayList<>();
        for (WorkOrder workOrder : store.values()) {
            if (workOrder.getStatus() == status) {
                result.add(workOrder);
            }
        }
        return result;
    }

    @Override
    public List<WorkOrder> findByProductCode(String productCode) {
        log.debug("Finding work orders by product code: {}", productCode);
        List<WorkOrder> result = new ArrayList<>();
        for (WorkOrder workOrder : store.values()) {
            if (workOrder.getProductInfo().getProductCode().equals(productCode)) {
                result.add(workOrder);
            }
        }
        return result;
    }
}
