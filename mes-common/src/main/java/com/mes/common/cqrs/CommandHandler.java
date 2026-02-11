package com.mes.common.cqrs;

/**
 * [CQRS Pattern: Command Handler]
 * [SOLID: SRP - 每個 Handler 只處理一種 Command]
 * [SOLID: OCP - 新增 Command 只需新增 Handler，不修改現有程式碼]
 *
 * CommandHandler 負責接收並處理一個特定的 Command。
 * 它是 Application Layer 的核心組件，協調 Domain Model 完成業務操作。
 *
 * @param <C> 處理的 Command 型別
 * @param <R> 回傳結果的型別（通常為 Void 或 ID）
 */
public interface CommandHandler<C extends Command, R> {

    /**
     * 處理命令。
     */
    R handle(C command);

    /**
     * 回傳此 Handler 可處理的 Command 類別。
     * 用於 CommandBus 的自動路由。
     */
    Class<C> getCommandType();
}
