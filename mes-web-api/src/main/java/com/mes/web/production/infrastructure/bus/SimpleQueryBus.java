package com.mes.web.production.infrastructure.bus;

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
 * [CQRS Pattern: Query Bus - 簡單實作]
 * [SOLID: DIP - 實作 QueryBus 介面，Controller 依賴抽象]
 * [SOLID: OCP - 新增 Query/Handler 不需修改此類別]
 * [SOLID: ISP - 與 CommandBus 分離，各自獨立]
 * [Design Pattern: Mediator - 解耦 Query 發送方與處理方]
 *
 * 使用 Map 儲存 Query 類別與對應 Handler 的映射關係。
 * 透過 Spring 的建構子注入自動收集所有 QueryHandler Bean。
 */
@Component
public class SimpleQueryBus implements QueryBus {

    private static final Logger log = LoggerFactory.getLogger(SimpleQueryBus.class);

    @SuppressWarnings("rawtypes")
    private final Map<Class, QueryHandler> handlerMap = new HashMap<Class, QueryHandler>();

    /**
     * 透過建構子注入所有 QueryHandler Bean，自動建立路由表。
     *
     * @param handlers Spring 容器中所有的 QueryHandler Bean
     */
    @SuppressWarnings("rawtypes")
    public SimpleQueryBus(List<QueryHandler> handlers) {
        for (QueryHandler handler : handlers) {
            handlerMap.put(handler.getQueryType(), handler);
            log.info("已註冊 Query Handler: {} -> {}",
                    handler.getQueryType().getSimpleName(),
                    handler.getClass().getSimpleName());
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <Q extends Query<R>, R> R dispatch(Q query) {
        QueryHandler handler = handlerMap.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "找不到對應的 QueryHandler: " + query.getClass().getSimpleName());
        }
        log.debug("派送 Query: {} -> {}",
                query.getClass().getSimpleName(),
                handler.getClass().getSimpleName());
        return (R) handler.handle(query);
    }
}
