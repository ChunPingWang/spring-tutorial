package com.mes.web.production.adapter.in.web;

import com.mes.common.cqrs.QueryBus;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.web.production.application.query.dto.ProductionLineView;
import com.mes.web.production.application.query.dto.ProductionRecordView;
import com.mes.web.production.application.query.dto.ProductionSummaryView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [Hexagonal Architecture: Inbound Adapter 測試]
 * [CQRS Pattern: Query Side 控制器測試]
 *
 * 使用 @WebMvcTest 只載入 Web 層，mock QueryBus。
 * 測試：
 * 1. GET 端點正確性
 * 2. 404 Not Found 處理
 * 3. 回應資料結構
 */
@DisplayName("ProductionQueryController 測試")
@WebMvcTest(ProductionQueryController.class)
class ProductionQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueryBus queryBus;

    @Nested
    @DisplayName("GET /api/v1/productions/{id} - 查詢單筆生產紀錄")
    class GetProductionRecordTests {

        @Test
        @DisplayName("存在的紀錄應回傳 200 OK 與資料")
        void shouldReturn200WithData() throws Exception {
            // Arrange
            ProductionRecordView view = createTestView("PR-001", "WO-001", "PROD-A");
            when(queryBus.dispatch(any())).thenReturn(view);

            // Act & Assert
            mockMvc.perform(get("/api/v1/productions/PR-001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value("PR-001"))
                    .andExpect(jsonPath("$.data.workOrderId").value("WO-001"))
                    .andExpect(jsonPath("$.data.productCode").value("PROD-A"))
                    .andExpect(jsonPath("$.data.status").value("RUNNING"))
                    .andExpect(jsonPath("$.data.goodQuantity").value(90))
                    .andExpect(jsonPath("$.data.defectiveQuantity").value(8))
                    .andExpect(jsonPath("$.data.productionLine.lineId").value("LINE-A"))
                    .andExpect(jsonPath("$.data.productionLine.lineName").value("A 產線"));
        }

        @Test
        @DisplayName("不存在的紀錄應回傳 404 Not Found")
        void shouldReturn404WhenNotFound() throws Exception {
            // Arrange
            when(queryBus.dispatch(any()))
                    .thenThrow(new EntityNotFoundException("ProductionRecord", "NOT-EXIST"));

            // Act & Assert
            mockMvc.perform(get("/api/v1/productions/NOT-EXIST"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("ProductionRecord not found with id: NOT-EXIST"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/productions/line/{lineId} - 依產線查詢")
    class ListByLineTests {

        @Test
        @DisplayName("應回傳該產線的生產紀錄列表")
        void shouldReturnListForLine() throws Exception {
            // Arrange
            ProductionRecordView view1 = createTestView("PR-001", "WO-001", "PROD-A");
            ProductionRecordView view2 = createTestView("PR-002", "WO-002", "PROD-B");
            when(queryBus.dispatch(any())).thenReturn(Arrays.asList(view1, view2));

            // Act & Assert
            mockMvc.perform(get("/api/v1/productions/line/LINE-A"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value("PR-001"))
                    .andExpect(jsonPath("$.data[1].id").value("PR-002"));
        }

        @Test
        @DisplayName("無紀錄時應回傳空列表")
        void shouldReturnEmptyListWhenNoRecords() throws Exception {
            // Arrange
            when(queryBus.dispatch(any())).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/api/v1/productions/line/LINE-X"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        @DisplayName("可帶狀態參數過濾")
        void shouldSupportStatusFilter() throws Exception {
            // Arrange
            when(queryBus.dispatch(any())).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/api/v1/productions/line/LINE-A")
                            .param("status", "RUNNING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/productions/summary - 生產摘要")
    class SummaryTests {

        @Test
        @DisplayName("應回傳生產摘要統計資料")
        void shouldReturnSummary() throws Exception {
            // Arrange
            ProductionSummaryView summary = new ProductionSummaryView(
                    10, 900, 80, new BigDecimal("91.84"));
            when(queryBus.dispatch(any())).thenReturn(summary);

            // Act & Assert
            mockMvc.perform(get("/api/v1/productions/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalRecords").value(10))
                    .andExpect(jsonPath("$.data.totalGood").value(900))
                    .andExpect(jsonPath("$.data.totalDefective").value(80))
                    .andExpect(jsonPath("$.data.overallYieldRate").value(91.84));
        }
    }

    // ========== 輔助方法 ==========

    private ProductionRecordView createTestView(String id, String workOrderId, String productCode) {
        ProductionRecordView view = new ProductionRecordView();
        view.setId(id);
        view.setWorkOrderId(workOrderId);
        view.setProductCode(productCode);
        view.setStatus("RUNNING");
        view.setStatusDescription("進行中");
        view.setProductionLine(new ProductionLineView("LINE-A", "A 產線"));
        view.setGoodQuantity(90);
        view.setDefectiveQuantity(8);
        view.setReworkQuantity(2);
        view.setTotalQuantity(100);
        view.setYieldRate(new BigDecimal("90.00"));
        view.setOperatorId("OP-001");
        view.setOperatorName("王小明");
        view.setShiftCode("DAY");
        view.setCreatedAt(LocalDateTime.now());
        view.setUpdatedAt(LocalDateTime.now());
        return view;
    }
}
