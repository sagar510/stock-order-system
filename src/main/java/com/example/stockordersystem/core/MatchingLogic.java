package com.example.stockordersystem.core;

import com.example.stockordersystem.constants.OrderStatus;
import com.example.stockordersystem.models.Order;
import com.example.stockordersystem.models.Trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MatchingLogic {

    public static List<Trade> matchBuy(Order buyOrder, OrderBook orderBook, TradeExecutor tradeExecutor) {
        List<Trade> trades = new ArrayList<>();
        PriorityQueue<Order> sellOrders = orderBook.getSellOrders();

        while (buyOrder.getRemainingQuantity() > 0 && !sellOrders.isEmpty()) {
            Order bestSell = sellOrders.peek();

            if (buyOrder.getPrice().compareTo(bestSell.getPrice()) < 0) break;

            int matchedQty = Math.min(buyOrder.getRemainingQuantity(), bestSell.getRemainingQuantity());
            BigDecimal executionPrice = bestSell.getPrice(); // resting sell

            Trade trade = tradeExecutor.executeTrade(buyOrder, bestSell, matchedQty, executionPrice);
            trades.add(trade);

            buyOrder.setRemainingQuantity(buyOrder.getRemainingQuantity() - matchedQty);
            bestSell.setRemainingQuantity(bestSell.getRemainingQuantity() - matchedQty);

            if (buyOrder.getRemainingQuantity() == 0) buyOrder.setStatus(OrderStatus.FILLED);
            else buyOrder.setStatus(OrderStatus.PARTIAL);

            if (bestSell.getRemainingQuantity() == 0) {
                bestSell.setStatus(OrderStatus.FILLED);
                sellOrders.poll();
            } else {
                bestSell.setStatus(OrderStatus.PARTIAL);
            }
        }

        if (buyOrder.getRemainingQuantity() > 0) {
            orderBook.addOrder(buyOrder);
        }

        return trades;
    }

    public static List<Trade> matchSell(Order sellOrder, OrderBook orderBook, TradeExecutor tradeExecutor) {
        List<Trade> trades = new ArrayList<>();
        PriorityQueue<Order> buyOrders = orderBook.getBuyOrders();

        while (sellOrder.getRemainingQuantity() > 0 && !buyOrders.isEmpty()) {
            Order bestBuy = buyOrders.peek();

            if (bestBuy.getPrice().compareTo(sellOrder.getPrice()) < 0) break;

            int matchedQty = Math.min(sellOrder.getRemainingQuantity(), bestBuy.getRemainingQuantity());
            BigDecimal executionPrice = bestBuy.getPrice(); // resting buy

            Trade trade = tradeExecutor.executeTrade(bestBuy, sellOrder, matchedQty, executionPrice);
            trades.add(trade);

            sellOrder.setRemainingQuantity(sellOrder.getRemainingQuantity() - matchedQty);
            bestBuy.setRemainingQuantity(bestBuy.getRemainingQuantity() - matchedQty);

            if (sellOrder.getRemainingQuantity() == 0) sellOrder.setStatus(OrderStatus.FILLED);
            else sellOrder.setStatus(OrderStatus.PARTIAL);

            if (bestBuy.getRemainingQuantity() == 0) {
                bestBuy.setStatus(OrderStatus.FILLED);
                buyOrders.poll();
            } else {
                bestBuy.setStatus(OrderStatus.PARTIAL);
            }
        }

        if (sellOrder.getRemainingQuantity() > 0) {
            orderBook.addOrder(sellOrder);
        }

        return trades;
    }
}
