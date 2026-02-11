package com.mes.kafka.quality.domain.model;

/**
 * [DDD Pattern: Value Object - Enum]
 * [SOLID: OCP - 新增檢驗類型只需增加列舉值]
 *
 * 檢驗類型列舉，定義品質檢驗的分類：
 * <ul>
 *   <li>INCOMING - 進料檢驗：原物料入庫前的品質確認</li>
 *   <li>IN_PROCESS - 製程中檢驗：生產過程中的即時品質監控</li>
 *   <li>FINAL - 最終檢驗：成品出貨前的最後品質把關</li>
 *   <li>SAMPLING - 抽樣檢驗：依統計抽樣計畫進行的品質檢驗</li>
 * </ul>
 */
public enum InspectionType {

    /** 進料檢驗 */
    INCOMING,

    /** 製程中檢驗 */
    IN_PROCESS,

    /** 最終檢驗 */
    FINAL,

    /** 抽樣檢驗 */
    SAMPLING
}
