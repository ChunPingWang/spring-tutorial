package com.mes.boot.workorder.application.service;

import com.mes.boot.workorder.application.assembler.WorkOrderAssembler;
import com.mes.boot.workorder.application.dto.CreateWorkOrderRequest;
import com.mes.boot.workorder.application.dto.WorkOrderResponse;
import com.mes.boot.workorder.domain.model.Quantity;
import com.mes.boot.workorder.domain.model.WorkOrder;
import com.mes.boot.workorder.domain.model.WorkOrderId;
import com.mes.boot.workorder.domain.model.WorkOrderStatus;
import com.mes.boot.workorder.domain.repository.WorkOrderRepository;
import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import com.mes.common.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * [DDD Pattern: Application Service]
 * [SOLID: SRP - 只負責協調領域物件、Repository 與事件發佈的流程]
 * [SOLID: DIP - 依賴抽象（Repository、DomainEventPublisher 介面），不依賴具體實作]
 * [Hexagonal Architecture: 此服務是 Use Case 的入口點]
 *
 * 工單應用服務，協調工單的建立、狀態轉換、查詢等使用案例（Use Cases）。
 *
 * Application Service 的職責：
 * <ul>
 *   <li>接收外部請求（DTO）</li>
 *   <li>呼叫領域物件執行業務邏輯</li>
 *   <li>透過 Repository 進行持久化</li>
 *   <li>發佈領域事件</li>
 *   <li>將結果轉換為 DTO 回傳</li>
 * </ul>
 *
 * 注意：Application Service 不應包含業務邏輯，業務邏輯應在領域層中實作。
 */
@Service
public class WorkOrderApplicationService {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderApplicationService.class);

    private final WorkOrderRepository workOrderRepository;
    private final DomainEventPublisher domainEventPublisher;

    /**
     * 建構子注入依賴。
     * [SOLID: DIP - 透過建構子注入抽象介面，方便測試與替換實作]
     *
     * @param workOrderRepository  工單 Repository
     * @param domainEventPublisher 領域事件發佈器
     */
    public WorkOrderApplicationService(WorkOrderRepository workOrderRepository,
                                       DomainEventPublisher domainEventPublisher) {
        this.workOrderRepository = workOrderRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    /**
     * 建立工單。
     *
     * @param request 建立工單請求 DTO
     * @return 已建立的工單回應 DTO
     */
    public WorkOrderResponse createWorkOrder(CreateWorkOrderRequest request) {
        log.info("Creating work order for product: {}", request.getProductCode());

        WorkOrder workOrder = WorkOrderAssembler.toDomain(request);
        workOrderRepository.save(workOrder);
        publishAndClearEvents(workOrder);

        log.info("Work order created: {}", workOrder.getId().getValue());
        return WorkOrderAssembler.toResponse(workOrder);
    }

    /**
     * 開始工單生產。
     *
     * @param id 工單 ID
     */
    public void startWorkOrder(String id) {
        log.info("Starting work order: {}", id);

        WorkOrder workOrder = findWorkOrderOrThrow(id);
        workOrder.start();
        workOrderRepository.save(workOrder);
        publishAndClearEvents(workOrder);

        log.info("Work order started: {}", id);
    }

    /**
     * 完成工單生產。
     *
     * @param id        工單 ID
     * @param completed 完成數量
     * @param defective 不良數量
     */
    public void completeWorkOrder(String id, int completed, int defective) {
        log.info("Completing work order: {}, completed={}, defective={}", id, completed, defective);

        WorkOrder workOrder = findWorkOrderOrThrow(id);
        Quantity actualQuantity = new Quantity(
                workOrder.getQuantity().getPlanned(), completed, defective);
        workOrder.complete(actualQuantity);
        workOrderRepository.save(workOrder);
        publishAndClearEvents(workOrder);

        log.info("Work order completed: {}", id);
    }

    /**
     * 取消工單。
     *
     * @param id     工單 ID
     * @param reason 取消原因
     */
    public void cancelWorkOrder(String id, String reason) {
        log.info("Cancelling work order: {}, reason: {}", id, reason);

        WorkOrder workOrder = findWorkOrderOrThrow(id);
        workOrder.cancel(reason);
        workOrderRepository.save(workOrder);
        publishAndClearEvents(workOrder);

        log.info("Work order cancelled: {}", id);
    }

    /**
     * 根據 ID 查詢工單。
     *
     * @param id 工單 ID
     * @return 工單回應 DTO
     * @throws EntityNotFoundException 若工單不存在
     */
    public WorkOrderResponse getWorkOrder(String id) {
        WorkOrder workOrder = findWorkOrderOrThrow(id);
        return WorkOrderAssembler.toResponse(workOrder);
    }

    /**
     * 查詢所有工單。
     *
     * @return 工單回應 DTO 列表
     */
    public List<WorkOrderResponse> listWorkOrders() {
        List<WorkOrder> workOrders = workOrderRepository.findAll();
        List<WorkOrderResponse> responses = new ArrayList<>();
        for (WorkOrder workOrder : workOrders) {
            responses.add(WorkOrderAssembler.toResponse(workOrder));
        }
        return responses;
    }

    /**
     * 根據狀態查詢工單。
     *
     * @param status 工單狀態字串（如 "CREATED"、"IN_PROGRESS"）
     * @return 符合條件的工單回應 DTO 列表
     */
    public List<WorkOrderResponse> listByStatus(String status) {
        WorkOrderStatus workOrderStatus = WorkOrderStatus.valueOf(status.toUpperCase());
        List<WorkOrder> workOrders = workOrderRepository.findByStatus(workOrderStatus);
        List<WorkOrderResponse> responses = new ArrayList<>();
        for (WorkOrder workOrder : workOrders) {
            responses.add(WorkOrderAssembler.toResponse(workOrder));
        }
        return responses;
    }

    // ─── Private helpers ───────────────────────────────────────

    private WorkOrder findWorkOrderOrThrow(String id) {
        WorkOrderId workOrderId = WorkOrderId.of(id);
        return workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder", id));
    }

    private void publishAndClearEvents(WorkOrder workOrder) {
        for (DomainEvent event : workOrder.getDomainEvents()) {
            domainEventPublisher.publish(event);
        }
        workOrder.clearEvents();
    }
}
