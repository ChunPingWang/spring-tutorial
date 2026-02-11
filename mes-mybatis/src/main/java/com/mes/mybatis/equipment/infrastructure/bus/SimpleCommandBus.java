package com.mes.mybatis.equipment.infrastructure.bus;

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
 * [CQRS Pattern: Simple Command Bus 實作]
 * [SOLID: OCP - 新增 CommandHandler 時不需修改此類別]
 *
 * 簡單的 CommandBus 實作，透過 Spring 自動注入所有 CommandHandler，
 * 並根據 Command 型別路由到對應的 Handler。
 *
 * 在生產環境中，可替換為支援中介軟體 (Middleware) 的實作，
 * 例如加入日誌、驗證、交易管理等橫切關注點。
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
            log.info("註冊 CommandHandler: {} -> {}", handler.getCommandType().getSimpleName(),
                    handler.getClass().getSimpleName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Command, R> R dispatch(C command) {
        CommandHandler<C, R> handler = handlerMap.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for command: " + command.getClass().getName());
        }
        log.debug("派送命令: {} -> {}", command.getClass().getSimpleName(),
                handler.getClass().getSimpleName());
        return handler.handle(command);
    }
}
