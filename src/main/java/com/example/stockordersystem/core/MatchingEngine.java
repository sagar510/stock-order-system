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

    /**
     * Place an order → match only within that stock's order book
     */
    public List<Trade> placeOrder(Order newOrder) {
        OrderBook book = orderBookManager.getOrderBook(newOrder.getStockId()); // same instance
        var lock = book.lock();             // lock belongs to the book
        lock.lock();                        // block until acquired
        try {
            if (newOrder.getType() == OrderType.BUY) {
                return MatchingLogic.matchBuy(newOrder, book, tradeExecutor);
            } else {
                return MatchingLogic.matchSell(newOrder, book, tradeExecutor);
            }
        } finally {
            lock.unlock();
        }
    }


    private List<Trade> matchBuyOrder(Order buyOrder, OrderBook orderBook) {
        return MatchingLogic.matchBuy(buyOrder, orderBook, tradeExecutor);
    }

    private List<Trade> matchSellOrder(Order sellOrder, OrderBook orderBook) {
        return MatchingLogic.matchSell(sellOrder, orderBook, tradeExecutor);
    }
}
