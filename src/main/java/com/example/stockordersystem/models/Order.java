package com.example.stockordersystem.models;

import com.example.stockordersystem.constants.OrderStatus;
import com.example.stockordersystem.constants.OrderType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Order {
    private String orderId;
    private String userId;      // optional, for tracking
    private String stockId;
    private OrderType type;     // BUY or SELL
    private BigDecimal price;
    private int quantity;
    private int remainingQuantity;
    private OrderStatus status;
    private LocalDateTime timestamp;

    private static final java.util.concurrent.atomic.AtomicLong SEQ_GEN = new java.util.concurrent.atomic.AtomicLong(0L);
    private final long seq; // assigned once

    public Order(String userId, String stockId, OrderType type, BigDecimal price, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        if (price == null || price.signum() <= 0) throw new IllegalArgumentException("price must be > 0");

        this.orderId = UUID.randomUUID().toString();
        this.userId = userId;
        this.stockId = stockId;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.PENDING;
        this.timestamp = LocalDateTime.now();
        this.seq = SEQ_GEN.getAndIncrement();
    }
}
