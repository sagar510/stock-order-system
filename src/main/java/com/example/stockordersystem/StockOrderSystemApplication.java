package com.example.stockordersystem;

import com.example.stockordersystem.core.search.SearchIndexManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StockOrderSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockOrderSystemApplication.class, args);
	}

    // Init search index at startup (mock for now)
    @Bean
    CommandLineRunner init(SearchIndexManager manager) {
        return args -> {
            manager.buildMock();
        };
    }
}
