package com.mes.web.production.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.web.production.application.query.ProductionSummaryQuery;
import com.mes.web.production.application.query.dto.ProductionSummaryView;
import com.mes.web.production.domain.model.OutputQuantity;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import com.mes.web.production.domain.service.ProductionDomainService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * [CQRS Pattern: Query Handler - 生產摘要查詢]
 * [SOLID: SRP - 只負責處理 ProductionSummaryQuery]
 * [Hexagonal Architecture: Application Service - 查詢端]
 *
 * 彙總所有生產紀錄的統計資料，包含總紀錄數、總良品數、總不良品數和整體良率。
 * 使用 ProductionDomainService 進行跨聚合的計算。
 */
@Component
public class ProductionSummaryQueryHandler
        implements QueryHandler<ProductionSummaryQuery, ProductionSummaryView> {

    private final ProductionRecordRepository repository;
    private final ProductionDomainService domainService;

    public ProductionSummaryQueryHandler(ProductionRecordRepository repository,
                                          ProductionDomainService domainService) {
        this.repository = repository;
        this.domainService = domainService;
    }

    @Override
    public ProductionSummaryView handle(ProductionSummaryQuery query) {
        List<ProductionRecord> allRecords = repository.findAll();

        int totalGood = 0;
        int totalDefective = 0;

        for (ProductionRecord record : allRecords) {
            OutputQuantity output = record.getOutput();
            totalGood += output.getGood();
            totalDefective += output.getDefective();
        }

        BigDecimal overallYieldRate = domainService.calculateYieldRate(allRecords);

        return new ProductionSummaryView(
                allRecords.size(),
                totalGood,
                totalDefective,
                overallYieldRate);
    }

    @Override
    public Class<ProductionSummaryQuery> getQueryType() {
        return ProductionSummaryQuery.class;
    }
}
