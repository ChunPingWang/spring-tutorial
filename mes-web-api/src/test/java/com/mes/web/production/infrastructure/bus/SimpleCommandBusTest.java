package com.mes.web.production.infrastructure.bus;

import com.mes.common.cqrs.Command;
import com.mes.common.cqrs.CommandHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [CQRS Pattern: Command Bus 單元測試]
 *
 * 測試 SimpleCommandBus 的路由機制：
 * 1. 正確路由 Command 到對應的 Handler
 * 2. 未註冊的 Command 應拋出例外
 */
@DisplayName("SimpleCommandBus 測試")
class SimpleCommandBusTest {

    @Test
    @DisplayName("應正確路由 Command 到對應的 Handler")
    void shouldRouteCommandToCorrectHandler() {
        // Arrange
        TestCommandHandler handler = new TestCommandHandler();
        SimpleCommandBus bus = new SimpleCommandBus(
                Collections.<CommandHandler>singletonList(handler));

        TestCommand command = new TestCommand("test-data");

        // Act
        String result = bus.dispatch(command);

        // Assert
        assertThat(result).isEqualTo("handled: test-data");
    }

    @Test
    @DisplayName("未註冊的 Command 應拋出 IllegalArgumentException")
    void shouldThrowForUnregisteredCommand() {
        // Arrange
        SimpleCommandBus bus = new SimpleCommandBus(
                Collections.<CommandHandler>emptyList());

        TestCommand command = new TestCommand("test");

        // Act & Assert
        assertThatThrownBy(() -> bus.dispatch(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("找不到對應的 CommandHandler");
    }

    @Test
    @DisplayName("應支援多個 Handler 的註冊")
    void shouldSupportMultipleHandlers() {
        // Arrange
        TestCommandHandler handler1 = new TestCommandHandler();
        AnotherCommandHandler handler2 = new AnotherCommandHandler();
        SimpleCommandBus bus = new SimpleCommandBus(
                Arrays.<CommandHandler>asList(handler1, handler2));

        // Act & Assert
        String result1 = bus.dispatch(new TestCommand("a"));
        assertThat(result1).isEqualTo("handled: a");

        String result2 = bus.dispatch(new AnotherCommand("b"));
        assertThat(result2).isEqualTo("another: b");
    }

    // ========== 測試用的 Command 和 Handler ==========

    static class TestCommand implements Command {
        private final String data;

        TestCommand(String data) {
            this.data = data;
        }

        String getData() {
            return data;
        }
    }

    static class TestCommandHandler implements CommandHandler<TestCommand, String> {
        @Override
        public String handle(TestCommand command) {
            return "handled: " + command.getData();
        }

        @Override
        public Class<TestCommand> getCommandType() {
            return TestCommand.class;
        }
    }

    static class AnotherCommand implements Command {
        private final String data;

        AnotherCommand(String data) {
            this.data = data;
        }

        String getData() {
            return data;
        }
    }

    static class AnotherCommandHandler implements CommandHandler<AnotherCommand, String> {
        @Override
        public String handle(AnotherCommand command) {
            return "another: " + command.getData();
        }

        @Override
        public Class<AnotherCommand> getCommandType() {
            return AnotherCommand.class;
        }
    }
}
