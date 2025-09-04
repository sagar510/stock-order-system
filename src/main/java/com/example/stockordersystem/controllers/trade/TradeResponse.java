package com.example.stockordersystem.controllers.trade;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Outgoing response DTO for a Trade record.
 */
@Data
public class TradeResponse {
    private String tradeId;
    private String buyOrderId;
    private String sellOrderId;
    private String stockId;
    private int quantity;
    private BigDecimal executionPrice;
    private LocalDateTime timestamp;
}
