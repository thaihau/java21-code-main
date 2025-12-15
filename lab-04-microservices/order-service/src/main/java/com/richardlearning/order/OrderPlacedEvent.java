package com.richardlearning.order;

// TODO 1: Create a record to hold order details (orderId, sku, quantity)
public record OrderPlacedEvent(
    Long orderId,
    String sku,
    Integer quantity
) {}