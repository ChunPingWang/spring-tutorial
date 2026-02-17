package com.mes.aop.application.service;

import com.mes.aop.domain.model.Order;
import com.mes.aop.domain.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    private final Map<String, Order> orderStore = new HashMap<>();

    @PerfMonitor("建立工單")
    public Order createOrder(String orderId, String productCode, int quantity) {
        Order order = new Order(orderId, productCode, quantity);
        orderStore.put(orderId, order);
        return order;
    }

    @LogExecutionTime
    public Order processOrder(Order order) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        order.process();
        return order;
    }

    @PerfMonitor("完成工單")
    public Order completeOrder(String orderId) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        order.complete();
        return order;
    }

    public Order getOrder(String orderId) {
        return orderStore.get(orderId);
    }
}
