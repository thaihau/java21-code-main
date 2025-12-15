package com.richardlearning.inventory;

// TODO 1: Create the exact same record as in Order Service (orderId, sku, quantity)
public record OrderPlacedEvent(
    Long orderId,
    String sku,
    Integer quantity
) {}