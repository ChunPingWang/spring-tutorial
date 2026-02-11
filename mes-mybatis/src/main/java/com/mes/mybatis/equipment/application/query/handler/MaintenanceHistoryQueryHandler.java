package com.mes.mybatis.equipment.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.mybatis.equipment.application.query.MaintenanceHistoryQuery;
import com.mes.mybatis.equipment.application.query.dto.MaintenanceHistoryView;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.dataobject.MaintenanceRecordDO;
import com.mes.mybatis.equipment.infrastructure.persistence.mybatis.mapper.MaintenanceRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * [CQRS Pattern: Query Handler - 查詢維護歷史（讀取路徑）]
 *
 * 直接使用 MaintenanceRecordMapper 查詢資料庫，繞過 Domain Model。
 * 這是 CQRS 讀取路徑的另一個範例。
 *
 * @see ListEquipmentByStatusQueryHandler 關於 CQRS 讀取路徑優化的詳細說明
 */
@Component
public class MaintenanceHistoryQueryHandler
        implements QueryHandler<MaintenanceHistoryQuery, List<MaintenanceHistoryView>> {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceHistoryQueryHandler.class);

    private final MaintenanceRecordMapper maintenanceRecordMapper;

    public MaintenanceHistoryQueryHandler(MaintenanceRecordMapper maintenanceRecordMapper) {
        this.maintenanceRecordMapper = maintenanceRecordMapper;
    }

    @Override
    public List<MaintenanceHistoryView> handle(MaintenanceHistoryQuery query) {
        log.info("查詢維護歷史 (CQRS 讀取路徑): equipmentId={}", query.getEquipmentId());

        // 直接使用 MyBatis Mapper 查詢，繞過 Domain Model
        List<MaintenanceRecordDO> records = maintenanceRecordMapper.selectByEquipmentId(query.getEquipmentId());

        List<MaintenanceHistoryView> views = new ArrayList<>();
        for (MaintenanceRecordDO record : records) {
            MaintenanceHistoryView view = new MaintenanceHistoryView();
            view.setId(record.getId());
            view.setEquipmentId(record.getEquipmentId());
            view.setMaintenanceType(record.getMaintenanceType());
            view.setDescription(record.getDescription());
            view.setScheduledDate(record.getScheduledDate());
            view.setCompletedDate(record.getCompletedDate());
            view.setTechnicianName(record.getTechnicianName());
            view.setStatus(record.getStatus());
            view.setCreatedAt(record.getCreatedAt());
            views.add(view);
        }

        return views;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<MaintenanceHistoryQuery> getQueryType() {
        return MaintenanceHistoryQuery.class;
    }
}
