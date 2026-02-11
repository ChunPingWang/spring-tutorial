package com.mes.web.production.application.command.handler;

import com.mes.web.production.application.command.StartProductionCommand;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionStatus;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * [CQRS Pattern: Command Handler 單元測試]
 *
 * 使用 Mockito mock Repository，驗證 Handler 的行為：
 * 1. 正確建立聚合根
 * 2. 啟動生產
 * 3. 透過 Repository 儲存
 * 4. 回傳新建的 ID
 */
@DisplayName("StartProductionCommandHandler 測試")
@ExtendWith(MockitoExtension.class)
class StartProductionCommandHandlerTest {

    @Mock
    private ProductionRecordRepository repository;

    private StartProductionCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new StartProductionCommandHandler(repository);
    }

    @Test
    @DisplayName("應建立新的生產紀錄並啟動")
    void shouldCreateAndStartProduction() {
        // Arrange
        StartProductionCommand command = new StartProductionCommand(
                "WO-001", "PROD-A",
                "LINE-A", "A 產線",
                "OP-001", "王小明", "DAY");

        // Act
        String resultId = handler.handle(command);

        // Assert - 回傳的 ID 不為空
        assertThat(resultId).isNotNull().isNotEmpty();

        // Assert - Repository 應被呼叫 save
        ArgumentCaptor<ProductionRecord> captor = ArgumentCaptor.forClass(ProductionRecord.class);
        verify(repository).save(captor.capture());

        ProductionRecord savedRecord = captor.getValue();
        assertThat(savedRecord.getWorkOrderId()).isEqualTo("WO-001");
        assertThat(savedRecord.getProductCode()).isEqualTo("PROD-A");
        assertThat(savedRecord.getStatus()).isEqualTo(ProductionStatus.RUNNING);
        assertThat(savedRecord.getProductionLine().getLineName()).isEqualTo("A 產線");
        assertThat(savedRecord.getOperator().getOperatorId()).isEqualTo("OP-001");
        assertThat(savedRecord.getOperator().getOperatorName()).isEqualTo("王小明");
        assertThat(savedRecord.getOperator().getShiftCode()).isEqualTo("DAY");
    }

    @Test
    @DisplayName("應正確回傳 Command 類型")
    void shouldReturnCorrectCommandType() {
        assertThat(handler.getCommandType()).isEqualTo(StartProductionCommand.class);
    }

    @Test
    @DisplayName("已啟動的生產紀錄應包含 ProductionStartedEvent")
    void shouldContainStartedEvent() {
        // Arrange
        StartProductionCommand command = new StartProductionCommand(
                "WO-002", "PROD-B",
                "LINE-B", "B 產線",
                "OP-002", "李大華", "NIGHT");

        // Act
        handler.handle(command);

        // Assert
        ArgumentCaptor<ProductionRecord> captor = ArgumentCaptor.forClass(ProductionRecord.class);
        verify(repository).save(captor.capture());

        ProductionRecord savedRecord = captor.getValue();
        assertThat(savedRecord.getDomainEvents()).hasSize(1);
    }
}
