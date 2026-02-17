package com.mes.aop.adapter.in.web;

import com.mes.aop.application.service.OrderService;
import com.mes.aop.domain.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        String orderId = (String) request.get("orderId");
        String productCode = (String) request.get("productCode");
        int quantity = (Integer) request.get("quantity");

        Order order = orderService.createOrder(orderId, productCode, quantity);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.getOrderId());
        response.put("productCode", order.getProductCode());
        response.put("quantity", order.getQuantity());
        response.put("status", order.getStatus().name());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/process")
    public ResponseEntity<Map<String, Object>> processOrder(@PathVariable String orderId) {
        Order order = orderService.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        Order processed = orderService.processOrder(order);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", processed.getOrderId());
        response.put("status", processed.getStatus().name());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Map<String, Object>> completeOrder(@PathVariable String orderId) {
        Order order = orderService.completeOrder(orderId);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.getOrderId());
        response.put("status", order.getStatus().name());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = orderService.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
}
