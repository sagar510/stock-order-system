package com.example.stockordersystem.core.search;

import com.example.stockordersystem.models.Stock;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
public class SearchIndexSnapshot {
    private final List<Stock> stocks;
    private final Map<String, Integer> byId;
    private final Map<String, Integer> byShortCode;
    private final Trie nameTrie;
    private final long rowCount;
    private final Instant builtAt;
    private final long buildMillis;

    public SearchIndexSnapshot(List<Stock> stocks,
                               Map<String, Integer> byId,
                               Map<String, Integer> byShortCode,
                               Trie nameTrie,
                               long buildMillis) {
        this.stocks = stocks;
        this.byId = byId;
        this.byShortCode = byShortCode;
        this.nameTrie = nameTrie;
        this.rowCount = stocks.size();
        this.builtAt = Instant.now();
        this.buildMillis = buildMillis;
    }

    public Stock getById(String id) {
        Integer idx = byId.get(id);
        return idx != null ? stocks.get(idx) : null;
    }

    public Stock getByShortCode(String code) {
        Integer idx = byShortCode.get(code.toUpperCase());
        return idx != null ? stocks.get(idx) : null;
    }

    public List<Stock> prefixSearch(String prefix, int limit) {
        List<Integer> matches = nameTrie.prefixSearch(prefix.toLowerCase(), limit);
        return matches.stream().map(stocks::get).toList();
    }
}
