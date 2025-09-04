package com.example.stockordersystem.services;

import com.example.stockordersystem.controllers.order.OrderRequest;
import com.example.stockordersystem.controllers.order.OrderResponse;
import com.example.stockordersystem.controllers.trade.TradeResponse;
import com.example.stockordersystem.core.MatchingEngine;
import com.example.stockordersystem.models.Order;
import com.example.stockordersystem.models.Trade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final MatchingEngine matchingEngine;

    public OrderService(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    public OrderResponse place(OrderRequest req) {
        // build domain Order
        Order order = new Order(
                req.getUserId(),
                req.getStockId(),
                req.getType(),
                req.getPrice(),
                req.getQuantity()
        );

        // run engine
        List<Trade> trades = matchingEngine.placeOrder(order);

        // map trades to response DTOs
        List<TradeResponse> tradeDtos = trades.stream().map(t -> {
            TradeResponse dto = new TradeResponse();
            dto.setTradeId(t.getTradeId());
            dto.setBuyOrderId(t.getBuyOrderId());
            dto.setSellOrderId(t.getSellOrderId());
            dto.setStockId(t.getStockId());
            dto.setQuantity(t.getQuantity());
            dto.setExecutionPrice(t.getExecutionPrice());
            dto.setTimestamp(t.getTimestamp());
            return dto;
        }).collect(Collectors.toList());

        // build response
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
        resp.setTrades(tradeDtos);

        return resp;
    }
}
