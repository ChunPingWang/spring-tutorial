package com.mes.mybatis.equipment.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.mybatis.equipment.application.assembler.EquipmentAssembler;
import com.mes.mybatis.equipment.application.query.GetEquipmentQuery;
import com.mes.mybatis.equipment.application.query.dto.EquipmentDetailView;
import com.mes.mybatis.equipment.domain.model.Equipment;
import com.mes.mybatis.equipment.domain.model.EquipmentId;
import com.mes.mybatis.equipment.domain.repository.EquipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Query Handler - 查詢設備詳情]
 *
 * 此 Handler 走「透過 Domain Model」的查詢路徑。
 * 從 Repository 載入完整的 Equipment 聚合根，再透過 Assembler 轉換為 DTO。
 *
 * 適用場景：需要經過領域邏輯處理或計算的查詢。
 */
@Component
public class GetEquipmentQueryHandler implements QueryHandler<GetEquipmentQuery, EquipmentDetailView> {

    private static final Logger log = LoggerFactory.getLogger(GetEquipmentQueryHandler.class);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentAssembler assembler;

    public GetEquipmentQueryHandler(EquipmentRepository equipmentRepository,
                                    EquipmentAssembler assembler) {
        this.equipmentRepository = equipmentRepository;
        this.assembler = assembler;
    }

    @Override
    public EquipmentDetailView handle(GetEquipmentQuery query) {
        log.info("查詢設備詳情: equipmentId={}", query.getEquipmentId());

        EquipmentId equipmentId = EquipmentId.of(query.getEquipmentId());
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipment", query.getEquipmentId()));

        return assembler.toDetailView(equipment);
    }

    @Override
    public Class<GetEquipmentQuery> getQueryType() {
        return GetEquipmentQuery.class;
    }
}
