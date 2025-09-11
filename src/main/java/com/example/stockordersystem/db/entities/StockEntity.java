package com.example.stockordersystem.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stocks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StockEntity {
    @Id private UUID id;
    @Column(unique = true, nullable=false) private String shortCode;
    @Column(nullable=false) private String name;
    private String sector;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // getters/setters
}
