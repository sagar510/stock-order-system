package com.example.stockordersystem.controllers.order;

import com.example.stockordersystem.core.OrderBookManager;
import com.example.stockordersystem.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService, OrderBookManager orderBookManager) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> place(@Valid @RequestBody OrderRequest request) {
        OrderResponse resp = orderService.place(request);
        return ResponseEntity.ok(resp);
    }

}
