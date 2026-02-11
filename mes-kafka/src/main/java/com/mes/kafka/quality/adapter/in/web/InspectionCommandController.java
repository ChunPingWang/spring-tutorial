package com.mes.kafka.quality.adapter.in.web;

import com.mes.common.cqrs.CommandBus;
import com.mes.kafka.quality.application.command.CompleteInspectionCommand;
import com.mes.kafka.quality.application.command.CreateInspectionCommand;
import com.mes.kafka.quality.application.command.RecordInspectionResultCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * [Hexagonal Architecture: Input Adapter - REST Command Controller]
 * [SOLID: SRP - 只負責處理檢驗工單的寫入操作（Command）]
 * [SOLID: DIP - 依賴 CommandBus 抽象，不直接依賴 Handler]
 * [CQRS Pattern: Command 端的 HTTP 入口]
 *
 * 檢驗工單命令控制器，處理所有寫入操作。
 * 與 {@link InspectionQueryController} 分離，體現 CQRS 的讀寫分離原則。
 */
@RestController
@RequestMapping("/api/v1/inspections")
public class InspectionCommandController {

    private static final Logger log = LoggerFactory.getLogger(InspectionCommandController.class);

    private final CommandBus commandBus;

    public InspectionCommandController(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    /**
     * 建立檢驗工單。
     *
     * @param request 包含 workOrderId, productCode, type 的請求
     * @return 新建立的檢驗工單 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createInspection(@RequestBody Map<String, String> request) {
        log.info("POST /api/v1/inspections - Creating inspection order");

        CreateInspectionCommand command = new CreateInspectionCommand(
                request.get("workOrderId"),
                request.get("productCode"),
                request.get("type"));

        String id = commandBus.dispatch(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inspection order created", id));
    }

    /**
     * 記錄檢驗結果。
     *
     * @param id      檢驗工單 ID
     * @param request 包含檢驗標準與量測值的請求
     * @return 操作結果
     */
    @PostMapping("/{id}/results")
    public ResponseEntity<ApiResponse<Void>> recordResult(
            @PathVariable String id, @RequestBody Map<String, Object> request) {
        log.info("POST /api/v1/inspections/{}/results - Recording inspection result", id);

        RecordInspectionResultCommand command = new RecordInspectionResultCommand(
                id,
                (String) request.get("standardCode"),
                toDouble(request.get("lowerBound")),
                toDouble(request.get("upperBound")),
                (String) request.get("unit"),
                toDouble(request.get("measuredValue")),
                (String) request.get("measuredUnit"),
                (String) request.get("inspector"));

        commandBus.dispatch(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("Inspection result recorded", null));
    }

    /**
     * 完成檢驗。
     *
     * @param id 檢驗工單 ID
     * @return 操作結果
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeInspection(@PathVariable String id) {
        log.info("PUT /api/v1/inspections/{}/complete - Completing inspection", id);

        CompleteInspectionCommand command = new CompleteInspectionCommand(id);
        commandBus.dispatch(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("Inspection completed", null));
    }

    private double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }
}
