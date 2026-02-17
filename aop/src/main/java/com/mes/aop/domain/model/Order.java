package com.mes.aop.domain.model;

public class Order {

    private String orderId;
    private String productCode;
    private int quantity;
    private OrderStatus status;

    public Order() {
    }

    public Order(String orderId, String productCode, int quantity) {
        this.orderId = orderId;
        this.productCode = productCode;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void process() {
        this.status = OrderStatus.PROCESSING;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }
}
