package com.example.stockordersystem.core;

import com.example.stockordersystem.constants.OrderType;
import com.example.stockordersystem.models.Order;
import com.example.stockordersystem.models.Trade;

import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {
    private final OrderBookManager orderBookManager;
    private final TradeExecutor tradeExecutor;

    public MatchingEngine(OrderBookManager orderBookManager, TradeExecutor tradeExecutor) {
        this.orderBookManager = orderBookManager;
        this.tradeExecutor = tradeExecutor;
    }

    public MatchingResult placeOrder(Order newOrder) {
        OrderBook book = orderBookManager.getOrderBook(newOrder.getStockId()); // same instance
        List<Trade> trades = new ArrayList<>();
        List<Order> changedOrders = new ArrayList<>();

        var lock = book.lock();             // lock belongs to the book
        lock.lock();                        // block until acquired
        try {
            if (newOrder.getType() == OrderType.BUY) {
                trades.addAll(MatchingLogic.matchBuy(newOrder, book, tradeExecutor, changedOrders));
            } else {
                trades.addAll(MatchingLogic.matchSell(newOrder, book, tradeExecutor, changedOrders));
            }

            return new MatchingResult(trades, changedOrders);
        } finally {
            lock.unlock();
        }
    }
}
