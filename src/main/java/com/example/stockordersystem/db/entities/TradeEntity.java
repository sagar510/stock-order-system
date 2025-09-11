package com.example.stockordersystem.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trades")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TradeEntity {
    @Id private UUID id;
    private UUID stockId;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private int quantity;
    @Column(precision=18, scale=8) private BigDecimal executionPrice;
    private LocalDateTime tsExecuted;
    // getters/setters
}
