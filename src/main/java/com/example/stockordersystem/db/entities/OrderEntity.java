package com.example.stockordersystem.db.entities;

import com.example.stockordersystem.constants.OrderStatus;
import com.example.stockordersystem.constants.OrderType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data // generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    private UUID id;

    private String userId;

    private UUID stockId;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Column(precision = 18, scale = 8)
    private BigDecimal price;

    private int quantity;

    private int remainingQuantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime tsCreated;

    private LocalDateTime tsUpdated;

    @PrePersist
    public void prePersist() {
        this.tsCreated = LocalDateTime.now();
        this.tsUpdated = this.tsCreated;
    }

    @PreUpdate
    public void preUpdate() {
        this.tsUpdated = LocalDateTime.now();
    }
}
