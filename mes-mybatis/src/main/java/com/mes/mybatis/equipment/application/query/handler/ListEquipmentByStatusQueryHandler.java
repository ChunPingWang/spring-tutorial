package com.mes.mybatis.equipment.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.mybatis.equipment.application.query.ListEquipmentByStatusQuery;
import com.mes.mybatis.equipment.application.query.dto.EquipmentSummaryView;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.EquipmentDO;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.mapper.EquipmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * [CQRS Pattern: Query Handler - 依狀態列出設備（讀取路徑優化）]
 *
 * ============================================================
 * 重要教學點：CQRS 讀取路徑優化 (Read Path Optimization)
 * ============================================================
 *
 * 此 Handler 直接使用 MyBatis Mapper（EquipmentMapper）查詢資料庫，
 * 完全繞過 Domain Model（Equipment 聚合根）。
 *
 * 為什麼可以這樣做？
 * 1. CQRS 的核心思想是「讀寫分離」— 讀取操作不需要經過寫入端的 Domain Model
 * 2. 讀取路徑不會改變任何狀態，因此不需要 Domain Model 的業務規則保護
 * 3. 直接從資料庫讀取 DO（Data Object）並轉換為 DTO，避免了不必要的領域物件建構
 * 4. 對於列表查詢等高頻讀取操作，這種方式效能更好
 *
 * 對比 GetEquipmentQueryHandler：
 * - GetEquipmentQueryHandler 透過 Repository -> Domain Model -> DTO（適合需要領域邏輯的查詢）
 * - 此 Handler 透過 Mapper -> DO -> DTO（適合純展示用的列表查詢）
 *
 * 這就是 CQRS 的威力：讀寫可以使用不同的路徑和模型，各自優化。
 * ============================================================
 */
@Component
public class ListEquipmentByStatusQueryHandler
        implements QueryHandler<ListEquipmentByStatusQuery, List<EquipmentSummaryView>> {

    private static final Logger log = LoggerFactory.getLogger(ListEquipmentByStatusQueryHandler.class);

    private final EquipmentMapper equipmentMapper;

    public ListEquipmentByStatusQueryHandler(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    public List<EquipmentSummaryView> handle(ListEquipmentByStatusQuery query) {
        log.info("查詢設備列表 (CQRS 讀取路徑): status={}", query.getStatus());

        // 直接使用 MyBatis Mapper 查詢，繞過 Domain Model
        List<EquipmentDO> dataObjects = equipmentMapper.selectByStatus(query.getStatus());

        // 直接將 DO 轉換為展示用 DTO，無需建構 Domain Object
        List<EquipmentSummaryView> views = new ArrayList<>();
        for (EquipmentDO dataObject : dataObjects) {
            EquipmentSummaryView view = new EquipmentSummaryView();
            view.setId(dataObject.getId());
            view.setName(dataObject.getName());
            view.setType(dataObject.getEquipmentType());
            view.setStatus(dataObject.getStatus());
            view.setLocationDescription(String.format("%s / %s樓 / %s區 / %s",
                    dataObject.getLocationBuilding(),
                    dataObject.getLocationFloor(),
                    dataObject.getLocationZone(),
                    dataObject.getLocationPosition()));
            views.add(view);
        }

        return views;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ListEquipmentByStatusQuery> getQueryType() {
        return ListEquipmentByStatusQuery.class;
    }
}
