package com.ecomarket.order.dto;

import java.math.BigDecimal;

public class CheckoutResponse {
    private Long orderId;
    private BigDecimal total;

    public CheckoutResponse() {}
    public CheckoutResponse(Long orderId, BigDecimal total) { this.orderId = orderId; this.total = total; }
    public Long getOrderId() { return orderId; }
    public BigDecimal getTotal() { return total; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
