package com.example.stockordersystem.core;

import com.example.stockordersystem.constants.OrderType;
import com.example.stockordersystem.models.Order;
import lombok.Data;

import java.util.Comparator;
import java.util.PriorityQueue;

@Data
public class OrderBook {

    private final java.util.concurrent.locks.ReentrantLock lock = new java.util.concurrent.locks.ReentrantLock(true);
    public java.util.concurrent.locks.ReentrantLock lock() { return lock; }

    private final PriorityQueue<Order> buyOrders;
    private final PriorityQueue<Order> sellOrders;

    public OrderBook() {
        // BUY orders: highest price first, then earliest timestamp
        buyOrders = new PriorityQueue<>(
                Comparator.comparing(Order::getPrice).reversed()
                        .thenComparing(Order::getTimestamp)
                        .thenComparingLong(Order::getSeq)
        );

        // SELL orders: lowest price first, then earliest timestamp
        sellOrders = new PriorityQueue<>(
                Comparator.comparing(Order::getPrice)
                        .thenComparing(Order::getTimestamp)
                        .thenComparingLong(Order::getSeq)
        );
    }

    /** Add a new order into the correct book */
    public void addOrder(Order order) {
        if (order.getType() == OrderType.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
    }

    /** Get BUY orders (priority queue) */
    public PriorityQueue<Order> getBuyOrders() {
        return buyOrders;
    }

    /** Get SELL orders (priority queue) */
    public PriorityQueue<Order> getSellOrders() {
        return sellOrders;
    }

    /** Debug helper: print current state */
    public void printOrderBook() {
        System.out.println("Buy Orders:");
        buyOrders.forEach(o -> System.out.println(
                o.getOrderId() + " BUY " + o.getRemainingQuantity() + " @ " + o.getPrice()));

        System.out.println("Sell Orders:");
        sellOrders.forEach(o -> System.out.println(
                o.getOrderId() + " SELL " + o.getRemainingQuantity() + " @ " + o.getPrice()));
    }
}
