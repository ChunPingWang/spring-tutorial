package com.mes.kafka.quality.infrastructure.bus;

import com.mes.common.cqrs.Command;
import com.mes.common.cqrs.CommandBus;
import com.mes.common.cqrs.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [CQRS Pattern: Command Bus 實作]
 * [SOLID: SRP - 只負責將 Command 路由到對應的 Handler]
 * [SOLID: OCP - 新增 Command 只需新增 Handler，無需修改此類別]
 * [SOLID: DIP - 依賴 CommandHandler 抽象介面]
 * [Design Pattern: Mediator - 解耦 Command 發送方與處理方]
 *
 * 簡單的 CommandBus 實作。
 * 透過 Spring 依賴注入自動收集所有 {@link CommandHandler}，
 * 並根據 Command 類型路由到對應的 Handler。
 */
@Component
public class SimpleCommandBus implements CommandBus {

    private static final Logger log = LoggerFactory.getLogger(SimpleCommandBus.class);

    @SuppressWarnings("rawtypes")
    private final Map<Class, CommandHandler> handlerMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public SimpleCommandBus(List<CommandHandler> handlers) {
        for (CommandHandler handler : handlers) {
            handlerMap.put(handler.getCommandType(), handler);
            log.info("Registered CommandHandler: {} -> {}",
                    handler.getCommandType().getSimpleName(), handler.getClass().getSimpleName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Command, R> R dispatch(C command) {
        CommandHandler<C, R> handler = handlerMap.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for command: " + command.getClass().getSimpleName());
        }
        log.debug("Dispatching command: {} -> {}",
                command.getClass().getSimpleName(), handler.getClass().getSimpleName());
        return handler.handle(command);
    }
}
