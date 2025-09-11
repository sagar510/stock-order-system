package com.example.stockordersystem.core;

import com.example.stockordersystem.constants.OrderType;
import com.example.stockordersystem.models.Order;
import com.example.stockordersystem.models.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingEngineTests {

    private MatchingEngine engine;

    @BeforeEach
    void setup() {
        OrderBookManager manager = new OrderBookManager();
        TradeExecutor executor = new TradeExecutor(null); // pass null if queueManager not wired for tests
        engine = new MatchingEngine(manager, executor);
    }

    @Test
    void test_exact_order_match() {
        // Given: BUY 1000 @ 100, then SELL 1000 @ 100 (same stock)
        Order buy = new Order("U1", "11111111-1111-1111-1111-111111111111", OrderType.BUY, BigDecimal.valueOf(100), 1000);
        MatchingResult r1 = engine.placeOrder(buy);
        assertTrue(r1.getTrades().isEmpty(), "No trades should fire on the first order (goes to book)");

        Order sell = new Order("U2", "11111111-1111-1111-1111-111111111111", OrderType.SELL, BigDecimal.valueOf(100), 1000);
        MatchingResult r2 = engine.placeOrder(sell);

        // Then: one trade executed for 1000 @ 100
        List<Trade> trades = r2.getTrades();
        assertEquals(1, trades.size(), "Exactly one trade should execute");
        Trade tr = trades.get(0);
        assertEquals("11111111-1111-1111-1111-111111111111", tr.getStockId());
        assertEquals(1000, tr.getQuantity());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(tr.getExecutionPrice()));

        // Orders updated
        assertEquals(0, sell.getRemainingQuantity());
        assertEquals(0, buy.getRemainingQuantity());
        assertTrue(r2.getUpdatedOrders().contains(buy));
        assertTrue(r2.getUpdatedOrders().contains(sell));
    }

    @Test
    void test_partial_order_match() {
        // Given: BUY 1000 @ 100, then SELL 600 @ 100
        Order buy = new Order("U1", "11111111-1111-1111-1111-111111111111", OrderType.BUY, BigDecimal.valueOf(100), 1000);
        MatchingResult r1 = engine.placeOrder(buy);
        assertTrue(r1.getTrades().isEmpty());

        Order sell = new Order("U2", "11111111-1111-1111-1111-111111111111", OrderType.SELL, BigDecimal.valueOf(100), 600);
        MatchingResult r2 = engine.placeOrder(sell);

        // Then: one trade for 600 @ 100
        List<Trade> trades = r2.getTrades();
        assertEquals(1, trades.size(), "Only one trade should execute");
        Trade tr = trades.get(0);
        assertEquals(600, tr.getQuantity());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(tr.getExecutionPrice()));

        // SELL is fully filled, BUY has 400 remaining
        assertEquals(0, sell.getRemainingQuantity(), "Sell should be fully filled");
        assertEquals(400, buy.getRemainingQuantity(), "Buy should have 400 remaining");
        assertTrue(r2.getUpdatedOrders().contains(buy));
        assertTrue(r2.getUpdatedOrders().contains(sell));
    }

    @Test
    void test_bulk_ordering() {
        String stockId = "11111111-1111-1111-1111-111111111111";

        int totalOrders = 10_000;
        int orderQty = 100;

        // Place 10,000 BUY + 10,000 SELL orders
        for (int i = 0; i < totalOrders; i++) {
            Order buy = new Order("U" + i, stockId, OrderType.BUY, BigDecimal.valueOf(100), orderQty);
            engine.placeOrder(buy);

            Order sell = new Order("S" + i, stockId, OrderType.SELL, BigDecimal.valueOf(100), orderQty);
            MatchingResult result = engine.placeOrder(sell);

            // Every SELL should match immediately with one BUY
            assertEquals(1, result.getTrades().size(), "Each SELL should execute exactly one trade");
        }

        // After all, total trades = 10,000
        // (because every BUY matched with one SELL)
        // Optionally, verify the last batch
        Order lastBuy = new Order("UX", stockId, OrderType.BUY, BigDecimal.valueOf(100), orderQty);
        MatchingResult r = engine.placeOrder(lastBuy);
        assertTrue(r.getTrades().isEmpty(), "Last BUY should stay pending since no SELL left");
    }

    @Test
    void test_no_match_when_prices_do_not_cross() {
        Order buy = new Order("U1", "AAPL", OrderType.BUY, BigDecimal.valueOf(99), 1000);
        assertTrue(engine.placeOrder(buy).getTrades().isEmpty());

        Order sell = new Order("U2", "AAPL", OrderType.SELL, BigDecimal.valueOf(101), 1000);
        MatchingResult result = engine.placeOrder(sell);

        assertTrue(result.getTrades().isEmpty(), "No trades should execute when prices do not cross");
        assertEquals(1000, buy.getRemainingQuantity());
        assertEquals(1000, sell.getRemainingQuantity());
    }

    @Test
    void test_orders_for_different_stocks_do_not_match() {
        Order buyAapl = new Order("U1", "AAPL", OrderType.BUY, BigDecimal.valueOf(100), 1000);
        assertTrue(engine.placeOrder(buyAapl).getTrades().isEmpty());

        Order sellGoog = new Order("U2", "GOOG", OrderType.SELL, BigDecimal.valueOf(100), 1000);
        MatchingResult result = engine.placeOrder(sellGoog);

        assertTrue(result.getTrades().isEmpty(), "Different stockIds must never match");
        assertEquals(1000, buyAapl.getRemainingQuantity());
        assertEquals(1000, sellGoog.getRemainingQuantity());
    }
}
