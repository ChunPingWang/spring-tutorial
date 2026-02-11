-- ============================================================
-- Module 3: MyBatis 持久層 - 設備管理 Schema
-- ============================================================

-- 設備主表
CREATE TABLE IF NOT EXISTS equipment (
    id                VARCHAR(36)  PRIMARY KEY,
    name              VARCHAR(100) NOT NULL,
    equipment_type    VARCHAR(50)  NOT NULL,
    status            VARCHAR(50)  NOT NULL DEFAULT 'IDLE',
    location_building VARCHAR(50)  NOT NULL,
    location_floor    VARCHAR(10)  NOT NULL,
    location_zone     VARCHAR(50)  NOT NULL,
    location_position VARCHAR(50)  NOT NULL,
    param_temperature DOUBLE       DEFAULT 0.0,
    param_pressure    DOUBLE       DEFAULT 0.0,
    param_speed       DOUBLE       DEFAULT 0.0,
    param_vibration   DOUBLE       DEFAULT 0.0,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 維護記錄表
CREATE TABLE IF NOT EXISTS maintenance_record (
    id                VARCHAR(36)  PRIMARY KEY,
    equipment_id      VARCHAR(36)  NOT NULL,
    maintenance_type  VARCHAR(50)  NOT NULL,
    description       VARCHAR(500) NOT NULL,
    scheduled_date    DATE         NOT NULL,
    completed_date    DATE,
    technician_name   VARCHAR(100),
    status            VARCHAR(50)  NOT NULL DEFAULT 'SCHEDULED',
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maintenance_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_equipment_status ON equipment(status);
CREATE INDEX IF NOT EXISTS idx_equipment_type ON equipment(equipment_type);
CREATE INDEX IF NOT EXISTS idx_maintenance_equipment_id ON maintenance_record(equipment_id);
