-- ============================================================
-- Module 3: MyBatis 持久層 - 設備管理 初始資料
-- 使用 MERGE INTO 確保可重複執行（冪等性）
-- ============================================================

-- 設備 1: CNC 數控機床
MERGE INTO equipment (id, name, equipment_type, status,
    location_building, location_floor, location_zone, location_position,
    param_temperature, param_pressure, param_speed, param_vibration,
    created_at, updated_at)
KEY (id)
VALUES ('EQ-001', 'CNC 加工中心 A1', 'CNC', 'RUNNING',
    'A棟', '1', '加工區', 'A1-01',
    45.5, 2.1, 3000.0, 0.05,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 設備 2: 組裝線
MERGE INTO equipment (id, name, equipment_type, status,
    location_building, location_floor, location_zone, location_position,
    param_temperature, param_pressure, param_speed, param_vibration,
    created_at, updated_at)
KEY (id)
VALUES ('EQ-002', '主組裝線 B1', 'ASSEMBLY_LINE', 'IDLE',
    'B棟', '1', '組裝區', 'B1-01',
    25.0, 1.0, 500.0, 0.02,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 設備 3: 工業機器人
MERGE INTO equipment (id, name, equipment_type, status,
    location_building, location_floor, location_zone, location_position,
    param_temperature, param_pressure, param_speed, param_vibration,
    created_at, updated_at)
KEY (id)
VALUES ('EQ-003', '焊接機器人 R1', 'ROBOT', 'RUNNING',
    'A棟', '2', '焊接區', 'A2-05',
    60.0, 3.5, 1500.0, 0.08,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 設備 4: 輸送帶
MERGE INTO equipment (id, name, equipment_type, status,
    location_building, location_floor, location_zone, location_position,
    param_temperature, param_pressure, param_speed, param_vibration,
    created_at, updated_at)
KEY (id)
VALUES ('EQ-004', '物料輸送帶 C1', 'CONVEYOR', 'MAINTENANCE',
    'C棟', '1', '物流區', 'C1-03',
    30.0, 0.5, 200.0, 0.03,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 設備 5: 檢測設備
MERGE INTO equipment (id, name, equipment_type, status,
    location_building, location_floor, location_zone, location_position,
    param_temperature, param_pressure, param_speed, param_vibration,
    created_at, updated_at)
KEY (id)
VALUES ('EQ-005', 'AOI 光學檢測機 I1', 'INSPECTION', 'IDLE',
    'B棟', '2', '品檢區', 'B2-01',
    22.0, 1.0, 100.0, 0.01,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 維護記錄
MERGE INTO maintenance_record (id, equipment_id, maintenance_type, description,
    scheduled_date, completed_date, technician_name, status, created_at)
KEY (id)
VALUES ('MR-001', 'EQ-001', 'PREVENTIVE', 'CNC 主軸定期保養',
    '2025-01-15', '2025-01-15', '張技師', 'COMPLETED', CURRENT_TIMESTAMP);

MERGE INTO maintenance_record (id, equipment_id, maintenance_type, description,
    scheduled_date, completed_date, technician_name, status, created_at)
KEY (id)
VALUES ('MR-002', 'EQ-004', 'PREVENTIVE', '輸送帶皮帶張力調整',
    '2025-02-01', NULL, NULL, 'SCHEDULED', CURRENT_TIMESTAMP);

MERGE INTO maintenance_record (id, equipment_id, maintenance_type, description,
    scheduled_date, completed_date, technician_name, status, created_at)
KEY (id)
VALUES ('MR-003', 'EQ-003', 'PREVENTIVE', '焊接機器人校準',
    '2025-01-20', '2025-01-21', '李技師', 'COMPLETED', CURRENT_TIMESTAMP);
