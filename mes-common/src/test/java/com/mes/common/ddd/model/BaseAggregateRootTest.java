package com.mes.common.ddd.model;

import com.mes.common.ddd.event.BaseDomainEvent;
import com.mes.common.ddd.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseAggregateRoot - 聚合根基礎行為測試")
class BaseAggregateRootTest {

    @Test
    @DisplayName("registerEvent - 應將事件加入 domainEvents 列表")
    void registerEvent_shouldAddEventToDomainEventsList() {
        TestAggregate agg = new TestAggregate("id-1");
        TestDomainEvent event = new TestDomainEvent("id-1");

        agg.doSomethingThatRaisesEvent(event);

        assertThat(agg.getDomainEvents()).containsExactly(event);
    }

    @Test
    @DisplayName("registerEvent - 多次註冊應累積事件")
    void registerEvent_multipleTimes_shouldAccumulateEvents() {
        TestAggregate agg = new TestAggregate("id-1");

        agg.doSomethingThatRaisesEvent(new TestDomainEvent("id-1"));
        agg.doSomethingThatRaisesEvent(new TestDomainEvent("id-1"));

        assertThat(agg.getDomainEvents()).hasSize(2);
    }

    @Test
    @DisplayName("clearEvents - 應清空所有已註冊的事件")
    void clearEvents_shouldEmptyDomainEventsList() {
        TestAggregate agg = new TestAggregate("id-1");
        agg.doSomethingThatRaisesEvent(new TestDomainEvent("id-1"));
        agg.doSomethingThatRaisesEvent(new TestDomainEvent("id-1"));

        agg.clearEvents();

        assertThat(agg.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("getDomainEvents - 回傳的列表應為不可修改")
    void getDomainEvents_shouldReturnUnmodifiableList() {
        TestAggregate agg = new TestAggregate("id-1");
        agg.doSomethingThatRaisesEvent(new TestDomainEvent("id-1"));

        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> agg.getDomainEvents().add(new TestDomainEvent("id-1"))
        );
    }

    @Test
    @DisplayName("Entity 相等性 - 相同 ID 的聚合根應相等")
    void equality_sameId_shouldBeEqual() {
        TestAggregate agg1 = new TestAggregate("id-1");
        TestAggregate agg2 = new TestAggregate("id-1");

        assertThat(agg1).isEqualTo(agg2);
        assertThat(agg1.hashCode()).isEqualTo(agg2.hashCode());
    }

    @Test
    @DisplayName("Entity 相等性 - 不同 ID 的聚合根不應相等")
    void equality_differentId_shouldNotBeEqual() {
        TestAggregate agg1 = new TestAggregate("id-1");
        TestAggregate agg2 = new TestAggregate("id-2");

        assertThat(agg1).isNotEqualTo(agg2);
    }

    // -- Test doubles --

    static class TestAggregate extends BaseAggregateRoot<String> {
        TestAggregate(String id) {
            super(id);
        }

        void doSomethingThatRaisesEvent(DomainEvent event) {
            registerEvent(event);
        }
    }

    static class TestDomainEvent extends BaseDomainEvent {
        TestDomainEvent(String aggregateId) {
            super(aggregateId);
        }
    }
}
