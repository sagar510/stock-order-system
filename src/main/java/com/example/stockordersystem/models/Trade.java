package com.example.stockordersystem.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    private String tradeId;

    private String buyOrderId;
    private String sellOrderId;

    private String stockId;

    private int quantity;               // matched quantity (shares)
    private BigDecimal executionPrice;  // actual price at which a buy order and a sell order get matched

    private LocalDateTime timestamp;

    public Trade(String buyOrderId,
                 String sellOrderId,
                 String stockId,
                 int quantity,
                 BigDecimal executionPrice) {

        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        if (executionPrice == null || executionPrice.signum() <= 0) {
            throw new IllegalArgumentException("executionPrice must be > 0");
        }

        this.tradeId = UUID.randomUUID().toString();
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.executionPrice = executionPrice;
        this.timestamp = LocalDateTime.now();
    }

    /** Convenience: total trade value */
    public BigDecimal getTradeAmount() {
        return executionPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
