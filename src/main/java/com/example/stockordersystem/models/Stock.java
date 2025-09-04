package com.example.stockordersystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    private String stockId;
    private String shortCode; // e.g., AAPL or TSLA
    private String name;      // e.g., Apple Inc.
    private String sector;    // e.g., Tech

    public Stock(String shortCode, String name, String sector) {
        this.stockId = UUID.randomUUID().toString();
        this.shortCode = shortCode;
        this.name = name;
        this.sector = sector;
    }
}
