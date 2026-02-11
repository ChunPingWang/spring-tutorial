package com.mes.web.production.adapter.in.web;

import com.mes.common.cqrs.QueryBus;
import com.mes.web.production.application.query.GetProductionRecordQuery;
import com.mes.web.production.application.query.ListProductionByLineQuery;
import com.mes.web.production.application.query.ProductionSummaryQuery;
import com.mes.web.production.application.query.dto.ProductionRecordView;
import com.mes.web.production.application.query.dto.ProductionSummaryView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [Hexagonal Architecture: Inbound Adapter - REST 查詢控制器]
 * [CQRS Pattern: Query Side - 只處理讀取操作]
 * [SOLID: SRP - 只負責接收 HTTP 請求並轉發至 QueryBus]
 * [SOLID: ISP - 與 CommandController 分離，遵循介面隔離原則]
 *
 * 生產紀錄的查詢端 REST 控制器。
 * 所有讀取操作都透過 QueryBus 派送，不改變系統狀態。
 *
 * 端點設計：
 * - GET /api/v1/productions/{id}          — 查詢單筆生產紀錄
 * - GET /api/v1/productions/line/{lineId} — 依產線查詢生產紀錄
 * - GET /api/v1/productions/summary       — 查詢生產摘要
 */
@RestController
@RequestMapping("/api/v1/productions")
public class ProductionQueryController {

    private final QueryBus queryBus;

    public ProductionQueryController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    /**
     * 查詢單筆生產紀錄。
     *
     * @param id 生產紀錄 ID
     * @return 生產紀錄檢視
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductionRecordView>> getProductionRecord(
            @PathVariable String id) {
        GetProductionRecordQuery query = new GetProductionRecordQuery(id);
        ProductionRecordView view = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(view));
    }

    /**
     * 依產線查詢生產紀錄，可選擇性地以狀態過濾。
     *
     * @param lineId 產線 ID
     * @param status 狀態過濾（可選）
     * @return 生產紀錄檢視列表
     */
    @GetMapping("/line/{lineId}")
    public ResponseEntity<ApiResponse<List<ProductionRecordView>>> listByLine(
            @PathVariable String lineId,
            @RequestParam(required = false) String status) {
        ListProductionByLineQuery query = new ListProductionByLineQuery(lineId, status);
        List<ProductionRecordView> views = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(views));
    }

    /**
     * 查詢生產摘要統計。
     *
     * @return 生產摘要檢視
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ProductionSummaryView>> getSummary() {
        ProductionSummaryQuery query = new ProductionSummaryQuery();
        ProductionSummaryView summary = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
