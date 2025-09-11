package com.example.stockordersystem.core;

import com.example.stockordersystem.db.PersistenceQueueManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

    @Bean
    public OrderBookManager orderBookManager() {
        return new OrderBookManager();
    }

    @Bean
    public TradeExecutor tradeExecutor(PersistenceQueueManager queueManager) {
        return new TradeExecutor(queueManager);
    }

    @Bean
    public MatchingEngine matchingEngine(OrderBookManager orderBookManager,
                                         TradeExecutor tradeExecutor) {
        return new MatchingEngine(orderBookManager, tradeExecutor);
    }
}
