package com.mes.mybatis.equipment.application.query.dto;

/**
 * [CQRS Pattern: Read Model DTO - 設備摘要視圖]
 *
 * 包含設備的摘要資訊（id, name, type, status, location），
 * 用於列表展示，不包含完整的維護記錄。
 */
public class EquipmentSummaryView {

    private String id;
    private String name;
    private String type;
    private String status;
    private String locationDescription;

    public EquipmentSummaryView() {
    }

    public EquipmentSummaryView(String id, String name, String type,
                                String status, String locationDescription) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.locationDescription = locationDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }
}
