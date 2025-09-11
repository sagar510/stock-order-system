package com.example.stockordersystem.controllers.trade;

import com.example.stockordersystem.models.Trade;
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

    public static TradeResponse fromDomain(Trade trade) {
        TradeResponse dto = new TradeResponse();
        dto.setTradeId(trade.getTradeId());
        dto.setBuyOrderId(trade.getBuyOrderId());
        dto.setSellOrderId(trade.getSellOrderId());
        dto.setStockId(trade.getStockId());
        dto.setQuantity(trade.getQuantity());
        dto.setExecutionPrice(trade.getExecutionPrice());
        dto.setTimestamp(trade.getTimestamp());
        return dto;
    }
}
