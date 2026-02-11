package com.mes.kafka.quality.adapter.in.web;

import com.mes.common.cqrs.QueryBus;
import com.mes.kafka.quality.application.query.DefectStatisticsQuery;
import com.mes.kafka.quality.application.query.GetInspectionOrderQuery;
import com.mes.kafka.quality.application.query.dto.DefectStatisticsView;
import com.mes.kafka.quality.application.query.dto.InspectionOrderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * [Hexagonal Architecture: Input Adapter - REST Query Controller]
 * [SOLID: SRP - 只負責處理檢驗工單的讀取操作（Query）]
 * [SOLID: DIP - 依賴 QueryBus 抽象，不直接依賴 Handler]
 * [CQRS Pattern: Query 端的 HTTP 入口]
 *
 * 檢驗工單查詢控制器，處理所有唯讀操作。
 * 與 {@link InspectionCommandController} 分離，體現 CQRS 的讀寫分離原則。
 */
@RestController
@RequestMapping("/api/v1/inspections")
public class InspectionQueryController {

    private static final Logger log = LoggerFactory.getLogger(InspectionQueryController.class);

    private final QueryBus queryBus;

    public InspectionQueryController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    /**
     * 查詢檢驗工單詳情。
     *
     * @param id 檢驗工單 ID
     * @return 檢驗工單詳情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InspectionOrderView>> getInspectionOrder(@PathVariable String id) {
        log.info("GET /api/v1/inspections/{} - Querying inspection order", id);

        GetInspectionOrderQuery query = new GetInspectionOrderQuery(id);
        InspectionOrderView view = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(view));
    }

    /**
     * 查詢缺陷統計資訊。
     *
     * @param productCode 產品代碼（選填，用於篩選）
     * @return 缺陷統計資訊
     */
    @GetMapping("/statistics/defects")
    public ResponseEntity<ApiResponse<DefectStatisticsView>> getDefectStatistics(
            @RequestParam(required = false) String productCode) {
        log.info("GET /api/v1/inspections/statistics/defects - Querying defect statistics, productCode={}",
                productCode);

        DefectStatisticsQuery query = new DefectStatisticsQuery(productCode);
        DefectStatisticsView view = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(view));
    }
}
