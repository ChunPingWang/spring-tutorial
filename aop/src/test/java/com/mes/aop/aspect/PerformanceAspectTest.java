package com.mes.aop.aspect;

import com.mes.aop.MesAopApplication;
import com.mes.aop.application.service.OrderService;
import com.mes.aop.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MesAopApplication.class)
class PerformanceAspectTest {

    @Autowired
    private OrderService orderService;

    @Test
    void processOrder_shouldExecuteWithDelay() {
        Order order = new Order("ORD-001", "PRODUCT-A", 100);

        Order result = orderService.processOrder(order);

        assertThat(result.getStatus().name()).isEqualTo("PROCESSING");
    }

    @Test
    void createOrder_shouldReturnOrder() {
        Order order = orderService.createOrder("ORD-002", "PRODUCT-B", 50);

        assertThat(order).isNotNull();
        assertThat(order.getOrderId()).isEqualTo("ORD-002");
        assertThat(order.getProductCode()).isEqualTo("PRODUCT-B");
        assertThat(order.getQuantity()).isEqualTo(50);
    }

    @Test
    void completeOrder_shouldChangeStatus() {
        orderService.createOrder("ORD-003", "PRODUCT-C", 30);

        Order result = orderService.completeOrder("ORD-003");

        assertThat(result.getStatus().name()).isEqualTo("COMPLETED");
    }
}
