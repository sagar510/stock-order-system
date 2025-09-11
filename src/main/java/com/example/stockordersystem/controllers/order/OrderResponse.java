package com.example.stockordersystem.controllers.order;

import com.example.stockordersystem.constants.OrderStatus;
import com.example.stockordersystem.constants.OrderType;
import com.example.stockordersystem.controllers.trade.TradeResponse;
import com.example.stockordersystem.models.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Outgoing response DTO for an order placement.
 */
@Data
public class OrderResponse {
    private String orderId;
    private String userId;
    private String stockId;
    private OrderType type;
    private BigDecimal price;
    private int quantity;
    private int remainingQuantity;
    private OrderStatus status;
    private LocalDateTime timestamp;

    private List<TradeResponse> trades; // trades created by this order (if any)


    public static OrderResponse fromDomain(Order order, List<TradeResponse> trades) {
        OrderResponse resp = new OrderResponse();
        resp.setOrderId(order.getOrderId());
        resp.setUserId(order.getUserId());
        resp.setStockId(order.getStockId());
        resp.setType(order.getType());
        resp.setPrice(order.getPrice());
        resp.setQuantity(order.getQuantity());
        resp.setRemainingQuantity(order.getRemainingQuantity());
        resp.setStatus(order.getStatus());
        resp.setTimestamp(order.getTimestamp());
        resp.setTrades(trades);
        return resp;
    }
}
