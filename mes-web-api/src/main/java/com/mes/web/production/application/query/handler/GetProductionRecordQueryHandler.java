package com.mes.web.production.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.web.production.application.assembler.ProductionRecordAssembler;
import com.mes.web.production.application.query.GetProductionRecordQuery;
import com.mes.web.production.application.query.dto.ProductionRecordView;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionRecordId;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Query Handler - 取得單筆生產紀錄]
 * [SOLID: SRP - 只負責處理 GetProductionRecordQuery]
 * [Hexagonal Architecture: Application Service - 查詢端]
 *
 * 在 CQRS 的讀取端，Query Handler 可以繞過 Domain Model 直接查詢讀取模型。
 * 此處為教學目的，仍透過 Repository 讀取後用 Assembler 轉換為 DTO。
 */
@Component
public class GetProductionRecordQueryHandler
        implements QueryHandler<GetProductionRecordQuery, ProductionRecordView> {

    private final ProductionRecordRepository repository;

    public GetProductionRecordQueryHandler(ProductionRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProductionRecordView handle(GetProductionRecordQuery query) {
        ProductionRecordId recordId = ProductionRecordId.of(query.getId());
        ProductionRecord record = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ProductionRecord", query.getId()));

        return ProductionRecordAssembler.toView(record);
    }

    @Override
    public Class<GetProductionRecordQuery> getQueryType() {
        return GetProductionRecordQuery.class;
    }
}
