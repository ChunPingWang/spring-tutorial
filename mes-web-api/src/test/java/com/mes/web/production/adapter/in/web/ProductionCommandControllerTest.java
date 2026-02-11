package com.mes.web.production.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.common.cqrs.CommandBus;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.web.production.application.command.RecordOutputCommand;
import com.mes.web.production.application.command.StartProductionCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [Hexagonal Architecture: Inbound Adapter 測試]
 * [CQRS Pattern: Command Side 控制器測試]
 *
 * 使用 @WebMvcTest 只載入 Web 層，mock CommandBus。
 * 測試：
 * 1. POST 端點正確性
 * 2. PUT 端點正確性
 * 3. 請求驗證（400 Bad Request）
 * 4. 業務例外處理
 */
@DisplayName("ProductionCommandController 測試")
@WebMvcTest(ProductionCommandController.class)
class ProductionCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommandBus commandBus;

    @Nested
    @DisplayName("POST /api/v1/productions - 啟動生產")
    class StartProductionTests {

        @Test
        @DisplayName("合法請求應回傳 201 Created")
        void shouldReturn201ForValidRequest() throws Exception {
            // Arrange
            when(commandBus.<StartProductionCommand, String>dispatch(any()))
                    .thenReturn("PR-001");

            StartProductionCommand command = new StartProductionCommand(
                    "WO-001", "PROD-A",
                    "LINE-A", "A 產線",
                    "OP-001", "王小明", "DAY");

            // Act & Assert
            mockMvc.perform(post("/api/v1/productions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(201))
                    .andExpect(jsonPath("$.data").value("PR-001"));
        }

        @Test
        @DisplayName("缺少必要欄位應回傳 400 Bad Request")
        void shouldReturn400ForMissingFields() throws Exception {
            // Arrange - 空的 command，所有 @NotBlank 欄位都缺少
            String emptyJson = "{}";

            // Act & Assert
            mockMvc.perform(post("/api/v1/productions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(emptyJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("部分欄位缺失應回傳 400 Bad Request")
        void shouldReturn400ForPartialFields() throws Exception {
            // Arrange - 只提供 workOrderId
            String partialJson = "{\"workOrderId\":\"WO-001\"}";

            // Act & Assert
            mockMvc.perform(post("/api/v1/productions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(partialJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/productions/{id}/output - 記錄產出")
    class RecordOutputTests {

        @Test
        @DisplayName("合法請求應回傳 200 OK")
        void shouldReturn200ForValidRequest() throws Exception {
            // Arrange
            RecordOutputCommand command = new RecordOutputCommand("PR-001", 100, 5, 3);

            // Act & Assert
            mockMvc.perform(put("/api/v1/productions/PR-001/output")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("生產紀錄不存在應回傳 404")
        void shouldReturn404WhenRecordNotFound() throws Exception {
            // Arrange
            when(commandBus.dispatch(any()))
                    .thenThrow(new EntityNotFoundException("ProductionRecord", "NOT-EXIST"));

            RecordOutputCommand command = new RecordOutputCommand("NOT-EXIST", 100, 5, 3);

            // Act & Assert
            mockMvc.perform(put("/api/v1/productions/NOT-EXIST/output")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/productions/{id}/pause - 暫停生產")
    class PauseProductionTests {

        @Test
        @DisplayName("合法請求應回傳 200 OK")
        void shouldReturn200ForValidRequest() throws Exception {
            mockMvc.perform(put("/api/v1/productions/PR-001/pause"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("生產已暫停"));
        }

        @Test
        @DisplayName("非法狀態轉換應回傳 409 Conflict")
        void shouldReturn409ForInvalidStateTransition() throws Exception {
            // Arrange
            when(commandBus.dispatch(any()))
                    .thenThrow(new BusinessRuleViolationException("只有進行中狀態可以暫停生產"));

            // Act & Assert
            mockMvc.perform(put("/api/v1/productions/PR-001/pause"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/productions/{id}/complete - 完成生產")
    class CompleteProductionTests {

        @Test
        @DisplayName("合法請求應回傳 200 OK")
        void shouldReturn200ForValidRequest() throws Exception {
            mockMvc.perform(put("/api/v1/productions/PR-001/complete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("生產已完成"));
        }
    }
}
