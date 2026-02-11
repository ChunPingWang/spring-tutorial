package com.mes.kafka.quality.infrastructure.messaging.kafka.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * [Infrastructure: Kafka Stream 設定]
 * [SOLID: SRP - 只負責 Kafka Stream 的相關配置]
 *
 * Spring Cloud Stream Kafka 綁定設定。
 * 主要配置透過 application.yml 完成，此類別提供額外的程式化設定。
 *
 * <p>Binding 對應：</p>
 * <ul>
 *   <li>qualityEventsOut-out-0 → mes.quality.events（發送品質事件）</li>
 *   <li>qualityEventsIn-in-0 → mes.quality.events（接收品質事件）</li>
 *   <li>productionEventsIn-in-0 → mes.production.events（接收生產事件）</li>
 * </ul>
 *
 * <p>Spring Cloud Stream 函數式綁定命名規則：</p>
 * <pre>
 * &lt;functionName&gt;-&lt;in|out&gt;-&lt;index&gt;
 * 例如：qualityEventsIn-in-0 表示 qualityEventsIn 函數的第 0 個輸入
 * </pre>
 */
@Configuration
public class KafkaStreamConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaStreamConfig.class);

    // 主要配置透過 application.yml 完成
    // 此類別可擴展以下功能：
    // - 自訂 MessageConverter
    // - 自訂 PartitionKeyExtractor
    // - 自訂 ErrorHandler / DLQ 配置
}
