package com.example.stockordersystem.core;

import com.example.stockordersystem.db.PersistenceQueueManager;
import com.example.stockordersystem.db.entities.TradeEntity;
import com.example.stockordersystem.models.Order;
import com.example.stockordersystem.models.Trade;
import com.example.stockordersystem.util.TradeMapper;

import java.math.BigDecimal;

public class TradeExecutor {

    private final PersistenceQueueManager queueManager;

    public TradeExecutor(PersistenceQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public Trade executeTrade(Order buyOrder, Order sellOrder, int matchedQuantity, BigDecimal price) {
        // Build domain trade
        Trade trade = new Trade(
                buyOrder.getOrderId(),
                sellOrder.getOrderId(),
                buyOrder.getStockId(),
                matchedQuantity,
                price
        );

        // Map → enqueue async persistence
        TradeEntity entity = TradeMapper.toEntity(trade);

        if (queueManager != null) {
            queueManager.enqueueTrade(entity);
        }

        return trade;
    }
}
