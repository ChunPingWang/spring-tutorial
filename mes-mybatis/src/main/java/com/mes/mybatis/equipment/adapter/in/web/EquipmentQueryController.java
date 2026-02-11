package com.mes.mybatis.equipment.adapter.in.web;

import com.mes.common.cqrs.QueryBus;
import com.mes.mybatis.equipment.application.query.GetEquipmentQuery;
import com.mes.mybatis.equipment.application.query.ListEquipmentByStatusQuery;
import com.mes.mybatis.equipment.application.query.MaintenanceHistoryQuery;
import com.mes.mybatis.equipment.application.query.dto.EquipmentDetailView;
import com.mes.mybatis.equipment.application.query.dto.EquipmentSummaryView;
import com.mes.mybatis.equipment.application.query.dto.MaintenanceHistoryView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [Hexagonal Architecture: Input Adapter - 設備查詢 API]
 * [CQRS Pattern: Query Side Controller]
 * [SOLID: SRP - 只負責處理讀取操作（Query）]
 *
 * 與 EquipmentCommandController 分離，體現 CQRS 的讀寫分離精神。
 * 接收 HTTP 請求，轉換為 Query，透過 QueryBus 派送。
 */
@RestController
@RequestMapping("/api/equipment")
public class EquipmentQueryController {

    private final QueryBus queryBus;

    public EquipmentQueryController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    /**
     * 查詢單一設備詳情。
     * GET /api/equipment/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDetailView> getEquipment(@PathVariable("id") String id) {
        GetEquipmentQuery query = new GetEquipmentQuery(id);
        EquipmentDetailView result = queryBus.dispatch(query);
        return ResponseEntity.ok(result);
    }

    /**
     * 依狀態列出設備。
     * GET /api/equipment?status=IDLE
     */
    @GetMapping
    public ResponseEntity<List<EquipmentSummaryView>> listByStatus(
            @RequestParam("status") String status) {
        ListEquipmentByStatusQuery query = new ListEquipmentByStatusQuery(status);
        List<EquipmentSummaryView> result = queryBus.dispatch(query);
        return ResponseEntity.ok(result);
    }

    /**
     * 查詢設備的維護歷史。
     * GET /api/equipment/{id}/maintenance
     */
    @GetMapping("/{id}/maintenance")
    public ResponseEntity<List<MaintenanceHistoryView>> getMaintenanceHistory(
            @PathVariable("id") String id) {
        MaintenanceHistoryQuery query = new MaintenanceHistoryQuery(id);
        List<MaintenanceHistoryView> result = queryBus.dispatch(query);
        return ResponseEntity.ok(result);
    }
}
