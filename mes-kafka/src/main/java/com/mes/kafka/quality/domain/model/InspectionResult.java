package com.mes.kafka.quality.domain.model;

import com.mes.common.ddd.model.BaseEntity;

/**
 * [DDD Pattern: Entity - 聚合內部 Entity]
 * [SOLID: SRP - 只負責記錄單一檢驗項目的結果]
 *
 * 檢驗結果 Entity，記錄每一筆品質檢驗的測量結果。
 * 作為 {@link InspectionOrder} 聚合的內部 Entity，
 * 只能透過 Aggregate Root 來存取和修改。
 *
 * <p>建構時會自動根據品質標準與量測值判定合格與否：</p>
 * <ul>
 *   <li>量測值在標準範圍內 → passed = true</li>
 *   <li>量測值超出標準範圍 → passed = false</li>
 * </ul>
 */
public class InspectionResult extends BaseEntity<InspectionResultId> {

    private final QualityStandard standard;
    private final MeasuredValue measuredValue;
    private final boolean passed;
    private final DefectDetail defectDetail;

    /**
     * 建立檢驗結果，自動根據標準判定合格與否。
     *
     * @param id            檢驗結果 ID
     * @param standard      品質標準
     * @param measuredValue 量測值
     */
    public InspectionResult(InspectionResultId id, QualityStandard standard, MeasuredValue measuredValue) {
        super(id);
        this.standard = standard;
        this.measuredValue = measuredValue;
        this.passed = measuredValue.isWithinStandard(standard);
        this.defectDetail = null;
    }

    /**
     * 建立帶有缺陷詳情的檢驗結果。
     *
     * @param id            檢驗結果 ID
     * @param standard      品質標準
     * @param measuredValue 量測值
     * @param defectDetail  缺陷詳情（可為 null）
     */
    public InspectionResult(InspectionResultId id, QualityStandard standard,
                            MeasuredValue measuredValue, DefectDetail defectDetail) {
        super(id);
        this.standard = standard;
        this.measuredValue = measuredValue;
        this.passed = measuredValue.isWithinStandard(standard);
        this.defectDetail = defectDetail;
    }

    public QualityStandard getStandard() {
        return standard;
    }

    public MeasuredValue getMeasuredValue() {
        return measuredValue;
    }

    public boolean isPassed() {
        return passed;
    }

    public DefectDetail getDefectDetail() {
        return defectDetail;
    }

    @Override
    public String toString() {
        return "InspectionResult{" +
                "id=" + getId() +
                ", standard=" + standard.getStandardCode() +
                ", value=" + measuredValue.getValue() +
                ", passed=" + passed +
                '}';
    }
}
