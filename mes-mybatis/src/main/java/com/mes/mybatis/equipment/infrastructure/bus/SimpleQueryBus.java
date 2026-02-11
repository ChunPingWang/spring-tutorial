package com.mes.mybatis.equipment.infrastructure.bus;

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
 * [CQRS Pattern: Simple Query Bus 實作]
 * [SOLID: OCP - 新增 QueryHandler 時不需修改此類別]
 *
 * 簡單的 QueryBus 實作，透過 Spring 自動注入所有 QueryHandler，
 * 並根據 Query 型別路由到對應的 Handler。
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
            log.info("註冊 QueryHandler: {} -> {}", handler.getQueryType().getSimpleName(),
                    handler.getClass().getSimpleName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Q extends Query<R>, R> R dispatch(Q query) {
        QueryHandler<Q, R> handler = handlerMap.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for query: " + query.getClass().getName());
        }
        log.debug("派送查詢: {} -> {}", query.getClass().getSimpleName(),
                handler.getClass().getSimpleName());
        return handler.handle(query);
    }
}
