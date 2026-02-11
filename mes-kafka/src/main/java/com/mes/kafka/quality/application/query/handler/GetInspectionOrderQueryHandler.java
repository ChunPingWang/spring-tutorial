package com.mes.kafka.quality.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.kafka.quality.application.assembler.InspectionAssembler;
import com.mes.kafka.quality.application.query.GetInspectionOrderQuery;
import com.mes.kafka.quality.application.query.dto.InspectionOrderView;
import com.mes.kafka.quality.domain.model.InspectionOrder;
import com.mes.kafka.quality.domain.model.InspectionOrderId;
import com.mes.kafka.quality.domain.repository.InspectionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Query Handler - 查詢檢驗工單處理器]
 * [SOLID: SRP - 只負責處理 GetInspectionOrderQuery]
 * [SOLID: DIP - 依賴 Repository 抽象]
 *
 * 處理查詢檢驗工單的請求。
 * 使用 {@link InspectionAssembler} 將領域模型轉換為唯讀視圖。
 */
@Component
public class GetInspectionOrderQueryHandler implements QueryHandler<GetInspectionOrderQuery, InspectionOrderView> {

    private static final Logger log = LoggerFactory.getLogger(GetInspectionOrderQueryHandler.class);

    private final InspectionOrderRepository repository;
    private final InspectionAssembler assembler;

    public GetInspectionOrderQueryHandler(InspectionOrderRepository repository,
                                           InspectionAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @Override
    public InspectionOrderView handle(GetInspectionOrderQuery query) {
        log.info("Handling GetInspectionOrderQuery: id={}", query.getInspectionOrderId());

        InspectionOrderId orderId = new InspectionOrderId(query.getInspectionOrderId());
        InspectionOrder order = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("InspectionOrder", query.getInspectionOrderId()));

        return assembler.toView(order);
    }

    @Override
    public Class<GetInspectionOrderQuery> getQueryType() {
        return GetInspectionOrderQuery.class;
    }
}
