package com.mes.web.production.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.web.production.application.assembler.ProductionRecordAssembler;
import com.mes.web.production.application.query.ListProductionByLineQuery;
import com.mes.web.production.application.query.dto.ProductionRecordView;
import com.mes.web.production.domain.model.ProductionLineId;
import com.mes.web.production.domain.model.ProductionRecord;
import com.mes.web.production.domain.model.ProductionStatus;
import com.mes.web.production.domain.repository.ProductionRecordRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * [CQRS Pattern: Query Handler - 依產線查詢生產紀錄]
 * [SOLID: SRP - 只負責處理 ListProductionByLineQuery]
 * [Hexagonal Architecture: Application Service - 查詢端]
 *
 * 支援依產線 ID 查詢，可選擇性地以狀態過濾結果。
 */
@Component
public class ListProductionByLineQueryHandler
        implements QueryHandler<ListProductionByLineQuery, List<ProductionRecordView>> {

    private final ProductionRecordRepository repository;

    public ListProductionByLineQueryHandler(ProductionRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductionRecordView> handle(ListProductionByLineQuery query) {
        ProductionLineId lineId = ProductionLineId.of(query.getLineId());
        List<ProductionRecord> records = repository.findByLineId(lineId);

        // 若有指定狀態，進一步過濾
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            ProductionStatus statusFilter = ProductionStatus.valueOf(query.getStatus());
            List<ProductionRecord> filtered = new ArrayList<ProductionRecord>();
            for (ProductionRecord record : records) {
                if (record.getStatus() == statusFilter) {
                    filtered.add(record);
                }
            }
            records = filtered;
        }

        return ProductionRecordAssembler.toViewList(records);
    }

    @Override
    public Class<ListProductionByLineQuery> getQueryType() {
        return ListProductionByLineQuery.class;
    }
}
