package com.mes.kafka.quality.infrastructure.persistence;

import com.mes.kafka.quality.domain.model.InspectionOrder;
import com.mes.kafka.quality.domain.model.InspectionOrderId;
import com.mes.kafka.quality.domain.model.InspectionStatus;
import com.mes.kafka.quality.domain.repository.InspectionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [DDD Pattern: Repository - Adapter (出站配接器)]
 * [SOLID: LSP - 完整實作 InspectionOrderRepository 介面]
 * [SOLID: DIP - 領域層依賴抽象，此類別是具體實作]
 * [Hexagonal Architecture: Output Adapter - 持久化配接器]
 *
 * 使用記憶體儲存的 InspectionOrderRepository 實作。
 * 適用於開發測試環境，生產環境可替換為 JPA 或 MyBatis 實作。
 */
@Component
public class InMemoryInspectionOrderRepository implements InspectionOrderRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryInspectionOrderRepository.class);

    private final Map<InspectionOrderId, InspectionOrder> store = new ConcurrentHashMap<>();

    @Override
    public Optional<InspectionOrder> findById(InspectionOrderId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<InspectionOrder> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void save(InspectionOrder aggregate) {
        store.put(aggregate.getId(), aggregate);
        log.debug("Saved InspectionOrder: id={}", aggregate.getId().getValue());
    }

    @Override
    public void deleteById(InspectionOrderId id) {
        store.remove(id);
        log.debug("Deleted InspectionOrder: id={}", id.getValue());
    }

    @Override
    public List<InspectionOrder> findByWorkOrderId(String workOrderId) {
        List<InspectionOrder> result = new ArrayList<>();
        for (InspectionOrder order : store.values()) {
            if (workOrderId.equals(order.getWorkOrderId())) {
                result.add(order);
            }
        }
        return result;
    }

    @Override
    public List<InspectionOrder> findByStatus(InspectionStatus status) {
        List<InspectionOrder> result = new ArrayList<>();
        for (InspectionOrder order : store.values()) {
            if (status == order.getStatus()) {
                result.add(order);
            }
        }
        return result;
    }
}
