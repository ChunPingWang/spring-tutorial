package com.mes.kafka;

import com.mes.kafka.quality.domain.model.InspectionOrder;
import com.mes.kafka.quality.domain.model.InspectionOrderId;
import com.mes.kafka.quality.domain.model.InspectionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MesKafkaApplication 模組驗證測試。
 *
 * 注意：由於測試環境沒有實際的 Kafka broker，
 * 且 StreamBridge 為 final class 無法在目前 JDK 版本下 Mock，
 * 因此不使用 @SpringBootTest 載入完整上下文。
 *
 * 此測試驗證：
 * 1. 主要的領域類別可正確實例化
 * 2. MesKafkaApplication 類別存在且可被載入
 *
 * 完整的整合測試需要啟動 Kafka broker（如使用 Testcontainers）。
 */
@DisplayName("MesKafkaApplication 模組驗證測試")
class MesKafkaApplicationTest {

    @Test
    @DisplayName("主程式類別應可被載入")
    void applicationClassShouldBeLoadable() {
        // 驗證 MesKafkaApplication 類別可被正確載入
        assertThat(MesKafkaApplication.class).isNotNull();
        assertThat(MesKafkaApplication.class.getAnnotations()).isNotEmpty();
    }

    @Test
    @DisplayName("核心領域物件應可正確建立")
    void coreDomainObjectsShouldBeCreatable() {
        InspectionOrder order = new InspectionOrder(
                new InspectionOrderId("INS-TEST"),
                "WO-TEST",
                "PRODUCT-TEST",
                InspectionType.FINAL);

        assertThat(order.getId().getValue()).isEqualTo("INS-TEST");
        assertThat(order.getWorkOrderId()).isEqualTo("WO-TEST");
        assertThat(order.getProductCode()).isEqualTo("PRODUCT-TEST");
        assertThat(order.getType()).isEqualTo(InspectionType.FINAL);
    }
}
