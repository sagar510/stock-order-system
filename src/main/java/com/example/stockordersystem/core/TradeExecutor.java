package com.example.stockordersystem.core;

import com.example.stockordersystem.models.Order;
import com.example.stockordersystem.models.Trade;

import java.math.BigDecimal;

public class TradeExecutor {
    public Trade executeTrade(Order buyOrder, Order sellOrder, int matchedQuantity, BigDecimal price) {
        return new Trade(
                buyOrder.getOrderId(),
                sellOrder.getOrderId(),
                buyOrder.getStockId(),
                matchedQuantity,
                price
        );
    }
}
