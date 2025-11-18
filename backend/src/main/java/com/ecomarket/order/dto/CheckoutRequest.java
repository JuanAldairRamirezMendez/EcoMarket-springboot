package com.ecomarket.order.dto;

import java.util.List;

public class CheckoutRequest {

    public static class Item {
        private Long productId;
        private Integer quantity;
        public Long getProductId() { return productId; }
        public Integer getQuantity() { return quantity; }
        public void setProductId(Long productId) { this.productId = productId; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    private List<Item> items;
    private String shippingAddress;

    public List<Item> getItems() { return items; }
    public String getShippingAddress() { return shippingAddress; }
    public void setItems(List<Item> items) { this.items = items; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
