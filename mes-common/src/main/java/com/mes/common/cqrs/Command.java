package com.mes.common.cqrs;

/**
 * [CQRS Pattern: Command 標記介面]
 *
 * Command 代表一個改變系統狀態的意圖（寫入操作）。
 * 命名慣例：動詞 + 名詞 + Command，如 StartProductionCommand。
 *
 * Command 與 Query 的分離是 CQRS 的核心：
 * - Command: 改變狀態，不回傳資料（或只回傳 ID）
 * - Query: 讀取資料，不改變狀態
 */
public interface Command {
}
