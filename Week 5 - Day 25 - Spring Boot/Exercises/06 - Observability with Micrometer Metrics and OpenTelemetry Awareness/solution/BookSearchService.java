package com.library;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

/**
 * Service that performs book searches and records Micrometer metrics.
 *
 * Two metric types are demonstrated:
 *   Counter — a monotonically increasing count (total searches performed)
 *   Timer   — records call duration (how long each search took)
 *
 * Both are accessible via /actuator/metrics/<metric-name>.
 */
@Service
public class BookSearchService {

    private final MeterRegistry meterRegistry;
    private final Counter searchCounter;

    public BookSearchService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Build a named counter with a tag — tags allow filtering in dashboards
        // e.g. in Prometheus: library_book_searches_total{type="all"}
        this.searchCounter = Counter.builder("library.book.searches")
                .tag("type", "all")
                .description("Total number of book searches performed")
                .register(meterRegistry);
    }

    public String searchBooks(String query) {
        // Increment counter on every call
        searchCounter.increment();

        // Timer.record() wraps a Runnable — records wall-clock duration
        meterRegistry.timer("library.search.duration").record(() -> {
            try {
                Thread.sleep(50);   // Simulate search latency
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        return "Search results for: " + query;
    }
}
