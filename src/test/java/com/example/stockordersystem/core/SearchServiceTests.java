package com.example.stockordersystem.core;

import com.example.stockordersystem.core.search.SearchIndexManager;
import com.example.stockordersystem.models.Stock;
import com.example.stockordersystem.services.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTests {

    private SearchService service;

    @BeforeEach
    void setup() {
        SearchIndexManager manager = new SearchIndexManager();
        manager.buildMock(); // AAPL, APPF, MSFT
        service = new SearchService(manager);
    }

    @Test
    void getByShortCode_found_ok() {
        Stock s = service.getByShortCode("AAPL");
        assertNotNull(s);
        assertEquals("AAPL", s.getShortCode());
        assertEquals("Apple Inc.", s.getName());
    }

    @Test
    void getByShortCode_case_insensitive() {
        Stock s = service.getByShortCode("aapl");
        assertNotNull(s);
        assertEquals("AAPL", s.getShortCode());
    }

    @Test
    void getById_found_ok() {
        // grab a real id from snapshot via shortCode lookup
        Stock aapl = service.getByShortCode("AAPL");
        assertNotNull(aapl);
        Stock byId = service.getById(aapl.getStockId());
        assertNotNull(byId);
        assertEquals(aapl.getStockId(), byId.getStockId());
        assertEquals("AAPL", byId.getShortCode());
    }

    @Test
    void getById_not_found_returns_null() {
        Stock s = service.getById("does-not-exist");
        assertNull(s);
    }

    @Test
    void prefixSearch_basic_match() {
        List<Stock> out = service.prefixSearch("app", 10);
        assertFalse(out.isEmpty());
        assertTrue(out.stream().anyMatch(st -> "AAPL".equals(st.getShortCode())));
        assertTrue(out.stream().anyMatch(st -> "APPF".equals(st.getShortCode())));
    }

    @Test
    void prefixSearch_case_insensitive() {
        List<Stock> out = service.prefixSearch("ApP", 10);
        assertTrue(out.stream().anyMatch(st -> "AAPL".equals(st.getShortCode())));
    }

    @Test
    void prefixSearch_limit_enforced() {
        // with current mock, 2 matches for "app"; ask limit=1 → should return size 1
        List<Stock> out = service.prefixSearch("app", 1);
        assertEquals(1, out.size());
    }

    @Test
    void prefixSearch_no_results() {
        List<Stock> out = service.prefixSearch("zzz", 10);
        assertTrue(out.isEmpty());
    }
}
