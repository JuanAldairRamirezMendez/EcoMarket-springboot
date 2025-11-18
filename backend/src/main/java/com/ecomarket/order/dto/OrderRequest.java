package com.ecomarket.order.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrderRequest {

    @NotEmpty(message = "Order items cannot be empty")
    @Size(min = 1, message = "At least one item is required")
    private List<OrderItemRequest> items;

    @NotNull(message = "Shipping address is required")
    @Size(min = 10, max = 500, message = "Shipping address must be between 10 and 500 characters")
    private String shippingAddress;

    @Size(max = 500, message = "Billing address cannot exceed 500 characters")
    private String billingAddress;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    public OrderRequest() {}

    public OrderRequest(List<OrderItemRequest> items, String shippingAddress, String billingAddress, String notes) {
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.notes = notes;
    }

    // Getters and setters
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}