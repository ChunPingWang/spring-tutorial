package com.mes.web.production.infrastructure.bus;

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
 * [CQRS Pattern: Command Bus - 簡單實作]
 * [SOLID: DIP - 實作 CommandBus 介面，Controller 依賴抽象]
 * [SOLID: OCP - 新增 Command/Handler 不需修改此類別]
 * [Design Pattern: Mediator - 解耦 Command 發送方與處理方]
 *
 * 使用 Map 儲存 Command 類別與對應 Handler 的映射關係。
 * 透過 Spring 的建構子注入自動收集所有 CommandHandler Bean。
 *
 * 路由邏輯：
 * 1. 收到 Command 時，根據其 Class 查找對應的 Handler
 * 2. 若找到，委派 Handler 執行
 * 3. 若未找到，拋出 IllegalArgumentException
 */
@Component
public class SimpleCommandBus implements CommandBus {

    private static final Logger log = LoggerFactory.getLogger(SimpleCommandBus.class);

    @SuppressWarnings("rawtypes")
    private final Map<Class, CommandHandler> handlerMap = new HashMap<Class, CommandHandler>();

    /**
     * 透過建構子注入所有 CommandHandler Bean，自動建立路由表。
     *
     * @param handlers Spring 容器中所有的 CommandHandler Bean
     */
    @SuppressWarnings("rawtypes")
    public SimpleCommandBus(List<CommandHandler> handlers) {
        for (CommandHandler handler : handlers) {
            handlerMap.put(handler.getCommandType(), handler);
            log.info("已註冊 Command Handler: {} -> {}",
                    handler.getCommandType().getSimpleName(),
                    handler.getClass().getSimpleName());
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <C extends Command, R> R dispatch(C command) {
        CommandHandler handler = handlerMap.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "找不到對應的 CommandHandler: " + command.getClass().getSimpleName());
        }
        log.debug("派送 Command: {} -> {}",
                command.getClass().getSimpleName(),
                handler.getClass().getSimpleName());
        return (R) handler.handle(command);
    }
}
