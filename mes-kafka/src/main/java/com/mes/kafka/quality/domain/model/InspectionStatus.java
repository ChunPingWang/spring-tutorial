package com.mes.kafka.quality.domain.model;

/**
 * [DDD Pattern: Value Object - Enum]
 * [SOLID: OCP - 狀態轉換規則封裝在 Aggregate Root 中]
 *
 * 檢驗工單狀態列舉，定義檢驗的生命週期：
 * <ul>
 *   <li>PENDING - 待檢驗：工單已建立，等待開始</li>
 *   <li>IN_PROGRESS - 檢驗中：正在進行檢驗作業</li>
 *   <li>PASSED - 合格：所有檢驗項目通過</li>
 *   <li>FAILED - 不合格：存在不合格的檢驗項目</li>
 *   <li>ON_HOLD - 暫停：因異常狀況暫時停止檢驗</li>
 * </ul>
 *
 * 狀態轉換規則：
 * PENDING → IN_PROGRESS → PASSED / FAILED
 * IN_PROGRESS → ON_HOLD
 */
public enum InspectionStatus {

    /** 待檢驗 */
    PENDING,

    /** 檢驗中 */
    IN_PROGRESS,

    /** 合格 */
    PASSED,

    /** 不合格 */
    FAILED,

    /** 暫停 */
    ON_HOLD
}
