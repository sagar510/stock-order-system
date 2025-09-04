package com.example.stockordersystem.controllers.search;

import com.example.stockordersystem.models.Stock;
import com.example.stockordersystem.services.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class SearchController {
    private final SearchService service;

    public SearchController(SearchService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getById(@PathVariable String id,
                                         @RequestParam(defaultValue = "id") String by) {
        Stock s = "shortCode".equalsIgnoreCase(by) ? service.getByShortCode(id) : service.getById(id);
        return s != null ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Stock>> prefix(@RequestParam String query,
                                              @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.prefixSearch(query, limit));
    }
}

