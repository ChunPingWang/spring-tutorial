package com.mes.kafka.quality.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.kafka.quality.application.query.DefectStatisticsQuery;
import com.mes.kafka.quality.application.query.dto.DefectStatisticsView;
import com.mes.kafka.quality.domain.model.DefectDetail;
import com.mes.kafka.quality.domain.model.InspectionOrder;
import com.mes.kafka.quality.domain.model.InspectionResult;
import com.mes.kafka.quality.domain.repository.InspectionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [CQRS Pattern: Query Handler - 缺陷統計查詢處理器]
 * [SOLID: SRP - 只負責處理 DefectStatisticsQuery]
 * [SOLID: DIP - 依賴 Repository 抽象]
 *
 * 處理缺陷統計查詢，彙整所有（或指定產品的）檢驗工單的缺陷資訊。
 * 統計項目包括：總檢驗數、總缺陷數、整體不良率、最常見缺陷代碼。
 */
@Component
public class DefectStatisticsQueryHandler implements QueryHandler<DefectStatisticsQuery, DefectStatisticsView> {

    private static final Logger log = LoggerFactory.getLogger(DefectStatisticsQueryHandler.class);

    private final InspectionOrderRepository repository;

    public DefectStatisticsQueryHandler(InspectionOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public DefectStatisticsView handle(DefectStatisticsQuery query) {
        log.info("Handling DefectStatisticsQuery: productCode={}", query.getProductCode());

        List<InspectionOrder> orders = repository.findAll();

        // 依產品代碼篩選（若有指定）
        if (query.getProductCode() != null && !query.getProductCode().isEmpty()) {
            List<InspectionOrder> filtered = new ArrayList<>();
            for (InspectionOrder order : orders) {
                if (query.getProductCode().equals(order.getProductCode())) {
                    filtered.add(order);
                }
            }
            orders = filtered;
        }

        int totalInspections = 0;
        int totalDefects = 0;
        Map<String, Integer> defectCodeCounts = new HashMap<>();

        for (InspectionOrder order : orders) {
            for (InspectionResult result : order.getResults()) {
                totalInspections++;
                if (!result.isPassed()) {
                    totalDefects++;
                    DefectDetail defect = result.getDefectDetail();
                    if (defect != null) {
                        String code = defect.getDefectCode();
                        Integer count = defectCodeCounts.get(code);
                        defectCodeCounts.put(code, count == null ? 1 : count + 1);
                    }
                }
            }
        }

        double overallDefectRate = totalInspections > 0
                ? (double) totalDefects / totalInspections
                : 0.0;

        // 找出最常見的缺陷代碼（依次數排序，取前 5 個）
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(defectCodeCounts.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        List<String> topDefectCodes = new ArrayList<>();
        int limit = Math.min(entries.size(), 5);
        for (int i = 0; i < limit; i++) {
            topDefectCodes.add(entries.get(i).getKey());
        }

        return new DefectStatisticsView(totalInspections, totalDefects, overallDefectRate, topDefectCodes);
    }

    @Override
    public Class<DefectStatisticsQuery> getQueryType() {
        return DefectStatisticsQuery.class;
    }
}
