package com.mes.mybatis.equipment.adapter.in.web;

import com.mes.common.cqrs.CommandBus;
import com.mes.mybatis.equipment.application.command.RegisterEquipmentCommand;
import com.mes.mybatis.equipment.application.command.ReportBreakdownCommand;
import com.mes.mybatis.equipment.application.command.ScheduleMaintenanceCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * [Hexagonal Architecture: Input Adapter - 設備寫入 API]
 * [CQRS Pattern: Command Side Controller]
 * [SOLID: SRP - 只負責處理寫入操作（Command）]
 *
 * 接收 HTTP 請求，轉換為 Command，透過 CommandBus 派送。
 * Controller 不包含任何業務邏輯。
 */
@RestController
@RequestMapping("/api/equipment")
public class EquipmentCommandController {

    private final CommandBus commandBus;

    public EquipmentCommandController(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    /**
     * 註冊新設備。
     * POST /api/equipment
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> registerEquipment(@RequestBody RegisterEquipmentRequest request) {
        RegisterEquipmentCommand command = new RegisterEquipmentCommand(
                request.getName(), request.getType(),
                request.getBuilding(), request.getFloor(),
                request.getZone(), request.getPosition());

        String equipmentId = commandBus.dispatch(command);

        Map<String, String> response = new HashMap<>();
        response.put("id", equipmentId);
        response.put("message", "設備註冊成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 為設備排程維護。
     * POST /api/equipment/{id}/maintenance
     */
    @PostMapping("/{id}/maintenance")
    public ResponseEntity<Map<String, String>> scheduleMaintenance(
            @PathVariable("id") String id,
            @RequestBody ScheduleMaintenanceRequest request) {
        ScheduleMaintenanceCommand command = new ScheduleMaintenanceCommand(
                id, request.getDescription(), request.getScheduledDate());

        commandBus.dispatch(command);

        Map<String, String> response = new HashMap<>();
        response.put("message", "維護排程成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 回報設備故障。
     * POST /api/equipment/{id}/breakdown
     */
    @PostMapping("/{id}/breakdown")
    public ResponseEntity<Map<String, String>> reportBreakdown(
            @PathVariable("id") String id,
            @RequestBody ReportBreakdownRequest request) {
        ReportBreakdownCommand command = new ReportBreakdownCommand(id, request.getDescription());

        commandBus.dispatch(command);

        Map<String, String> response = new HashMap<>();
        response.put("message", "故障回報成功");
        return ResponseEntity.ok(response);
    }

    // ======================== Request DTOs ========================

    static class RegisterEquipmentRequest {
        private String name;
        private String type;
        private String building;
        private String floor;
        private String zone;
        private String position;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getBuilding() { return building; }
        public void setBuilding(String building) { this.building = building; }
        public String getFloor() { return floor; }
        public void setFloor(String floor) { this.floor = floor; }
        public String getZone() { return zone; }
        public void setZone(String zone) { this.zone = zone; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
    }

    static class ScheduleMaintenanceRequest {
        private String description;
        private LocalDate scheduledDate;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDate getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    }

    static class ReportBreakdownRequest {
        private String description;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
