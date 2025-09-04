package com.example.stockordersystem.controllers.order;

import com.example.stockordersystem.constants.OrderType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Incoming request DTO for placing a new order.
 */
@Data
public class OrderRequest {

    @NotBlank
    private String userId;    // user who placed the order

    @NotBlank
    private String stockId;   // stock to buy/sell

    @NotNull
    private OrderType type;   // BUY or SELL

    @NotNull
    @DecimalMin(value = "0.00000001", message = "Price must be greater than 0")
    private BigDecimal price;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;
}
