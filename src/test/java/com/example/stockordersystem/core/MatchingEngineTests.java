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
        TradeExecutor executor = new TradeExecutor();
        engine = new MatchingEngine(manager, executor);
    }

    @Test
    void test_exact_order_match() {
        // Given: BUY 1000 @ 100, then SELL 1000 @ 100 (same stock)
        Order buy = new Order("U1", "AAPL", OrderType.BUY, BigDecimal.valueOf(100), 1000);
        List<Trade> t1 = engine.placeOrder(buy);
        assertTrue(t1.isEmpty(), "No trades should fire on the first order (goes to book)");

        Order sell = new Order("U2", "AAPL", OrderType.SELL, BigDecimal.valueOf(100), 1000);
        List<Trade> t2 = engine.placeOrder(sell);

        // Then: one trade executed for 1000 @ 100
        assertEquals(1, t2.size(), "Exactly one trade should execute");
        Trade tr = t2.get(0);
        assertEquals("AAPL", tr.getStockId());
        assertEquals(1000, tr.getQuantity());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(tr.getExecutionPrice()));

        // Orders updated
        assertEquals(0, sell.getRemainingQuantity());
        assertEquals(0, buy.getRemainingQuantity());
        // Optionally verify statuses if exposed (FILLED etc.)
    }

    @Test
    void test_partial_order_match() {
        // Given: BUY 1000 @ 100, then SELL 600 @ 100
        Order buy = new Order("U1", "AAPL", OrderType.BUY, BigDecimal.valueOf(100), 1000);
        List<Trade> t1 = engine.placeOrder(buy);
        assertTrue(t1.isEmpty());

        Order sell = new Order("U2", "AAPL", OrderType.SELL, BigDecimal.valueOf(100), 600);
        List<Trade> t2 = engine.placeOrder(sell);

        // Then: one trade for 600 @ 100
        assertEquals(1, t2.size(), "Only one trade should execute");
        Trade tr = t2.get(0);
        assertEquals(600, tr.getQuantity());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(tr.getExecutionPrice()));

        // SELL is fully filled, BUY has 400 remaining
        assertEquals(0, sell.getRemainingQuantity(), "Sell should be fully filled");
        assertEquals(400, buy.getRemainingQuantity(), "Buy should have 400 remaining");
    }

    @Test
    void test_no_match_when_prices_do_not_cross() {
        Order buy = new Order("U1", "AAPL", OrderType.BUY, BigDecimal.valueOf(99), 1000);
        assertTrue(engine.placeOrder(buy).isEmpty());

        Order sell = new Order("U2", "AAPL", OrderType.SELL, BigDecimal.valueOf(101), 1000);
        List<Trade> trades = engine.placeOrder(sell);

        assertTrue(trades.isEmpty(), "No trades should execute when prices do not cross");
        assertEquals(1000, buy.getRemainingQuantity());
        assertEquals(1000, sell.getRemainingQuantity());
    }

    @Test
    void test_orders_for_different_stocks_do_not_match() {
        Order buyAapl = new Order("U1", "AAPL", OrderType.BUY, BigDecimal.valueOf(100), 1000);
        assertTrue(engine.placeOrder(buyAapl).isEmpty());

        Order sellGoog = new Order("U2", "GOOG", OrderType.SELL, BigDecimal.valueOf(100), 1000);
        List<Trade> trades = engine.placeOrder(sellGoog);

        assertTrue(trades.isEmpty(), "Different stockIds must never match");
        assertEquals(1000, buyAapl.getRemainingQuantity());
        assertEquals(1000, sellGoog.getRemainingQuantity());
    }
}
