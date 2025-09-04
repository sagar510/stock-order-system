package com.example.stockordersystem.core.search;

import com.example.stockordersystem.models.Stock;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SearchIndexManager {
    private volatile SearchIndexSnapshot current;

    public SearchIndexSnapshot get() {
        return current;
    }

    public void swap(SearchIndexSnapshot snapshot) {
        this.current = snapshot;
    }

    // Mock builder for now
    public void buildMock() {
        long start = System.currentTimeMillis();

        List<Stock> stocks = List.of(
                new Stock("AAPL", "Apple Inc.", "Tech"),
                new Stock("APPF", "AppFolio Inc.", "Tech"),
                new Stock("MSFT", "Microsoft Corp.", "Tech")
        );

        Map<String, Integer> byId = new HashMap<>();
        Map<String, Integer> byShort = new HashMap<>();
        Trie trie = new Trie();

        for (int i = 0; i < stocks.size(); i++) {
            Stock s = stocks.get(i);
            // normalize once
            String id = s.getStockId();                 // auto-generated UUID
            String sc = s.getShortCode().toUpperCase(); // normalize
            String nm = s.getName().toLowerCase();      // normalize

            byId.put(id, i);
            byShort.put(sc, i);
            trie.insert(nm, i);
        }

        SearchIndexSnapshot snapshot = new SearchIndexSnapshot(
                stocks, byId, byShort, trie,
                System.currentTimeMillis() - start
        );
        swap(snapshot);
    }
}
