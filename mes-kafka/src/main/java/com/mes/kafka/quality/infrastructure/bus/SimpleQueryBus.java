package com.mes.kafka.quality.infrastructure.bus;

import com.mes.common.cqrs.Query;
import com.mes.common.cqrs.QueryBus;
import com.mes.common.cqrs.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [CQRS Pattern: Query Bus 實作]
 * [SOLID: SRP - 只負責將 Query 路由到對應的 Handler]
 * [SOLID: OCP - 新增 Query 只需新增 Handler，無需修改此類別]
 * [SOLID: ISP - 與 CommandBus 分離，各自獨立]
 * [Design Pattern: Mediator - 解耦 Query 發送方與處理方]
 *
 * 簡單的 QueryBus 實作。
 * 透過 Spring 依賴注入自動收集所有 {@link QueryHandler}，
 * 並根據 Query 類型路由到對應的 Handler。
 */
@Component
public class SimpleQueryBus implements QueryBus {

    private static final Logger log = LoggerFactory.getLogger(SimpleQueryBus.class);

    @SuppressWarnings("rawtypes")
    private final Map<Class, QueryHandler> handlerMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public SimpleQueryBus(List<QueryHandler> handlers) {
        for (QueryHandler handler : handlers) {
            handlerMap.put(handler.getQueryType(), handler);
            log.info("Registered QueryHandler: {} -> {}",
                    handler.getQueryType().getSimpleName(), handler.getClass().getSimpleName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Q extends Query<R>, R> R dispatch(Q query) {
        QueryHandler<Q, R> handler = handlerMap.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for query: " + query.getClass().getSimpleName());
        }
        log.debug("Dispatching query: {} -> {}",
                query.getClass().getSimpleName(), handler.getClass().getSimpleName());
        return handler.handle(query);
    }
}
