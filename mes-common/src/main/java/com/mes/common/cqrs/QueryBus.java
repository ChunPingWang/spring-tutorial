package com.mes.common.cqrs;

/**
 * [CQRS Pattern: Query Bus]
 * [SOLID: ISP - 與 CommandBus 分離，各自獨立的介面]
 *
 * QueryBus 負責將 Query 路由到對應的 QueryHandler。
 * 與 CommandBus 分離是 Interface Segregation 的展現。
 */
public interface QueryBus {

    /**
     * 派送查詢到對應的 Handler 執行。
     *
     * @param query 要執行的查詢
     * @param <Q>   查詢型別
     * @param <R>   查詢結果型別
     * @return 查詢結果
     */
    <Q extends Query<R>, R> R dispatch(Q query);
}
