package com.mes.common.cqrs;

/**
 * [CQRS Pattern: Query 標記介面]
 *
 * Query 代表一個讀取系統狀態的請求（唯讀操作）。
 * 命名慣例：動詞 + 名詞 + Query，如 GetProductionRecordQuery。
 *
 * Query 不應該造成任何副作用（Side-Effect Free）。
 *
 * @param <R> 查詢結果的型別
 */
public interface Query<R> {
}
