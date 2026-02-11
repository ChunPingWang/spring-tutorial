package com.mes.common.cqrs;

/**
 * [CQRS Pattern: Command Bus]
 * [SOLID: DIP - Controller 依賴此抽象，不依賴具體 Handler]
 *
 * CommandBus 負責將 Command 路由到對應的 CommandHandler。
 * 它是 Mediator 模式的應用，解耦了 Command 的發送方與處理方。
 */
public interface CommandBus {

    /**
     * 派送命令到對應的 Handler 執行。
     *
     * @param command 要執行的命令
     * @param <C>     命令型別
     * @param <R>     回傳結果型別
     * @return 命令執行結果
     */
    <C extends Command, R> R dispatch(C command);
}
