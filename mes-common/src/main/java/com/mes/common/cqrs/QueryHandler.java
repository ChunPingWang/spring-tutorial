package com.mes.common.cqrs;

/**
 * [CQRS Pattern: Query Handler]
 * [SOLID: SRP - 每個 Handler 只處理一種 Query]
 *
 * QueryHandler 負責接收並處理一個特定的 Query。
 * 在 CQRS 中，Query Handler 可以繞過 Domain Model 直接從讀取模型取得資料，
 * 這是讀寫分離的關鍵教學點。
 *
 * @param <Q> 處理的 Query 型別
 * @param <R> 查詢結果的型別
 */
public interface QueryHandler<Q extends Query<R>, R> {

    /**
     * 處理查詢。
     */
    R handle(Q query);

    /**
     * 回傳此 Handler 可處理的 Query 類別。
     * 用於 QueryBus 的自動路由。
     */
    Class<Q> getQueryType();
}
