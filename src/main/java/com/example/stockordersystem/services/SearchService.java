package com.example.stockordersystem.services;

import com.example.stockordersystem.core.search.SearchIndexManager;
import com.example.stockordersystem.core.search.SearchIndexSnapshot;
import com.example.stockordersystem.models.Stock;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SearchService {
    private final SearchIndexManager manager;

    public SearchService(SearchIndexManager manager) {
        this.manager = manager;
    }

    public Stock getById(String id) {
        SearchIndexSnapshot idx = manager.get();
        if (idx == null) return null;
        return idx.getById(id);
    }

    public Stock getByShortCode(String code) {
        SearchIndexSnapshot idx = manager.get();
        if (idx == null) return null;
        return idx.getByShortCode(code);
    }

    public List<Stock> prefixSearch(String q, int limit) {
        SearchIndexSnapshot idx = manager.get();
        if (idx == null) return Collections.emptyList();
        return idx.prefixSearch(q, limit);
    }
}
