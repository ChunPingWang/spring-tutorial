package com.mes.common.ddd.model;

import com.mes.common.ddd.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [DDD Pattern: Aggregate Root 基礎類別]
 * [SOLID: OCP - 子類別可擴展行為而不修改此基礎類別]
 *
 * Aggregate Root 是聚合的入口點，負責：
 * 1. 維護聚合內部的一致性 (Invariants)
 * 2. 管理領域事件 (Domain Events) 的註冊與發佈
 * 3. 控制對聚合內部 Entity 的存取
 *
 * 所有狀態變更都應透過 Aggregate Root 的方法進行，
 * 並在適當時機註冊 Domain Event。
 *
 * @param <ID> 識別值的型別
 */
public abstract class BaseAggregateRoot<ID> extends BaseEntity<ID> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected BaseAggregateRoot() {
        super();
    }

    protected BaseAggregateRoot(ID id) {
        super(id);
    }

    /**
     * 註冊一個領域事件。
     * 事件會在聚合被持久化後由基礎設施層發佈。
     */
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * 取得所有已註冊的領域事件（唯讀）。
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 清除所有已註冊的領域事件。
     * 通常在事件被成功發佈後呼叫。
     */
    public void clearEvents() {
        domainEvents.clear();
    }
}
