package com.example.stockordersystem.services;

import com.example.stockordersystem.controllers.order.OrderRequest;
import com.example.stockordersystem.controllers.order.OrderResponse;
import com.example.stockordersystem.controllers.trade.TradeResponse;
import com.example.stockordersystem.core.MatchingEngine;
import com.example.stockordersystem.core.MatchingResult;
import com.example.stockordersystem.db.entities.OrderEntity;
import com.example.stockordersystem.db.repositories.jpa.OrderRepository;
import com.example.stockordersystem.models.Order;
import com.example.stockordersystem.util.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final MatchingEngine matchingEngine;
    private final OrderRepository orderRepository;

    public OrderService(MatchingEngine matchingEngine,
                        OrderRepository orderRepository) {
        this.matchingEngine = matchingEngine;
        this.orderRepository = orderRepository;
    }

    public OrderResponse place(OrderRequest req) {
        // 1️⃣ Build domain order
        Order order = new Order(
                req.getUserId(),
                req.getStockId(),
                req.getType(),
                req.getPrice(),
                req.getQuantity()
        );

        // 2️⃣ Save the new order synchronously
        OrderEntity entity = OrderMapper.toEntity(order);
        orderRepository.save(entity);

        // 3️⃣ Run engine → trades + changed orders
        MatchingResult result = matchingEngine.placeOrder(order);

        // 4️⃣ Persist all changed orders back to DB (sync)
        List<OrderEntity> changedEntities = result.getUpdatedOrders().stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
        orderRepository.saveAll(changedEntities);

        // 5️⃣ Map trades to response DTOs
        List<TradeResponse> tradeDtos = result.getTrades().stream()
                .map(TradeResponse::fromDomain)
                .collect(Collectors.toList());

        // 6️⃣ Build final response
        return OrderResponse.fromDomain(order, tradeDtos);
    }
}
