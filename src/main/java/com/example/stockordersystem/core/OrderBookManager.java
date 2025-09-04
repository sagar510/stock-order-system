package com.example.stockordersystem.core;

import com.example.stockordersystem.models.Order;

import java.util.concurrent.ConcurrentHashMap;

public class OrderBookManager {
    private final java.util.concurrent.ConcurrentHashMap<String, OrderBook> orderBooks = new ConcurrentHashMap<>();

    /** Get or create an OrderBook for a given stockId */
    public OrderBook getOrderBook(String stockId) {
        return orderBooks.computeIfAbsent(stockId, k -> new OrderBook());
    }

    /** Debug: print all order books */
    public void printAllBooks() {
        orderBooks.forEach((stock, book) -> {
            System.out.println("=== Order Book for " + stock + " ===");
            book.printOrderBook();
        });
    }
}
