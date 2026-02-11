package com.mes.web.production.infrastructure.bus;

import com.mes.common.cqrs.Query;
import com.mes.common.cqrs.QueryHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [CQRS Pattern: Query Bus 單元測試]
 *
 * 測試 SimpleQueryBus 的路由機制：
 * 1. 正確路由 Query 到對應的 Handler
 * 2. 未註冊的 Query 應拋出例外
 */
@DisplayName("SimpleQueryBus 測試")
class SimpleQueryBusTest {

    @Test
    @DisplayName("應正確路由 Query 到對應的 Handler")
    void shouldRouteQueryToCorrectHandler() {
        // Arrange
        TestQueryHandler handler = new TestQueryHandler();
        SimpleQueryBus bus = new SimpleQueryBus(
                Collections.<QueryHandler>singletonList(handler));

        TestQuery query = new TestQuery("test-id");

        // Act
        String result = bus.dispatch(query);

        // Assert
        assertThat(result).isEqualTo("result: test-id");
    }

    @Test
    @DisplayName("未註冊的 Query 應拋出 IllegalArgumentException")
    void shouldThrowForUnregisteredQuery() {
        // Arrange
        SimpleQueryBus bus = new SimpleQueryBus(
                Collections.<QueryHandler>emptyList());

        TestQuery query = new TestQuery("test");

        // Act & Assert
        assertThatThrownBy(() -> bus.dispatch(query))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("找不到對應的 QueryHandler");
    }

    @Test
    @DisplayName("應支援多個 Handler 的註冊")
    void shouldSupportMultipleHandlers() {
        // Arrange
        TestQueryHandler handler1 = new TestQueryHandler();
        AnotherQueryHandler handler2 = new AnotherQueryHandler();
        SimpleQueryBus bus = new SimpleQueryBus(
                Arrays.<QueryHandler>asList(handler1, handler2));

        // Act & Assert
        String result1 = bus.dispatch(new TestQuery("x"));
        assertThat(result1).isEqualTo("result: x");

        Integer result2 = bus.dispatch(new AnotherQuery(42));
        assertThat(result2).isEqualTo(84);
    }

    // ========== 測試用的 Query 和 Handler ==========

    static class TestQuery implements Query<String> {
        private final String id;

        TestQuery(String id) {
            this.id = id;
        }

        String getId() {
            return id;
        }
    }

    static class TestQueryHandler implements QueryHandler<TestQuery, String> {
        @Override
        public String handle(TestQuery query) {
            return "result: " + query.getId();
        }

        @Override
        public Class<TestQuery> getQueryType() {
            return TestQuery.class;
        }
    }

    static class AnotherQuery implements Query<Integer> {
        private final int value;

        AnotherQuery(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }
    }

    static class AnotherQueryHandler implements QueryHandler<AnotherQuery, Integer> {
        @Override
        public Integer handle(AnotherQuery query) {
            return query.getValue() * 2;
        }

        @Override
        public Class<AnotherQuery> getQueryType() {
            return AnotherQuery.class;
        }
    }
}
