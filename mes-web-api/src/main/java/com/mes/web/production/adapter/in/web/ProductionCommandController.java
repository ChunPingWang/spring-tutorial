package com.mes.web.production.adapter.in.web;

import com.mes.common.cqrs.CommandBus;
import com.mes.web.production.application.command.CompleteProductionCommand;
import com.mes.web.production.application.command.PauseProductionCommand;
import com.mes.web.production.application.command.RecordOutputCommand;
import com.mes.web.production.application.command.StartProductionCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * [Hexagonal Architecture: Inbound Adapter - REST 命令控制器]
 * [CQRS Pattern: Command Side - 只處理寫入操作]
 * [SOLID: SRP - 只負責接收 HTTP 請求並轉發至 CommandBus]
 * [SOLID: DIP - 依賴 CommandBus 介面而非具體 Handler]
 *
 * 生產紀錄的命令端 REST 控制器。
 * 所有寫入操作（新增、修改、刪除）都透過 CommandBus 派送。
 *
 * 端點設計：
 * - POST /api/v1/productions         — 啟動生產
 * - PUT  /api/v1/productions/{id}/output   — 記錄產出
 * - PUT  /api/v1/productions/{id}/pause    — 暫停生產
 * - PUT  /api/v1/productions/{id}/complete — 完成生產
 */
@RestController
@RequestMapping("/api/v1/productions")
public class ProductionCommandController {

    private final CommandBus commandBus;

    public ProductionCommandController(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    /**
     * 啟動新的生產。
     *
     * @param command 啟動生產命令
     * @return 新建的生產紀錄 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> startProduction(
            @Valid @RequestBody StartProductionCommand command) {
        String recordId = commandBus.dispatch(command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(recordId));
    }

    /**
     * 記錄產出數量。
     *
     * @param id      生產紀錄 ID
     * @param command 記錄產出命令
     * @return 操作結果
     */
    @PutMapping("/{id}/output")
    public ResponseEntity<ApiResponse<Void>> recordOutput(
            @PathVariable String id,
            @Valid @RequestBody RecordOutputCommand command) {
        command.setProductionRecordId(id);
        commandBus.dispatch(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("產出記錄成功", null));
    }

    /**
     * 暫停生產。
     *
     * @param id 生產紀錄 ID
     * @return 操作結果
     */
    @PutMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<Void>> pauseProduction(@PathVariable String id) {
        PauseProductionCommand command = new PauseProductionCommand(id);
        commandBus.dispatch(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("生產已暫停", null));
    }

    /**
     * 完成生產。
     *
     * @param id 生產紀錄 ID
     * @return 操作結果
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeProduction(@PathVariable String id) {
        CompleteProductionCommand command = new CompleteProductionCommand(id);
        commandBus.dispatch(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("生產已完成", null));
    }
}
