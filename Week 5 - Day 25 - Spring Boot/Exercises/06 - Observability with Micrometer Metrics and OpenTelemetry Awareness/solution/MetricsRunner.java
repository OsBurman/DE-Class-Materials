package com.library;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Calls BookSearchService three times and prints the accumulated counter value.
 * Also demonstrates reading a metric value programmatically from the MeterRegistry.
 */
@Component
public class MetricsRunner implements CommandLineRunner {

    private final BookSearchService bookSearchService;
    private final MeterRegistry meterRegistry;

    public MetricsRunner(BookSearchService bookSearchService, MeterRegistry meterRegistry) {
        this.bookSearchService = bookSearchService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void run(String... args) {
        // Perform three searches — each increments the counter by 1
        bookSearchService.searchBooks("spring boot");
        bookSearchService.searchBooks("java streams");
        bookSearchService.searchBooks("design patterns");

        // Read the counter value back from the registry — must use same name + tags
        double count = meterRegistry.counter("library.book.searches", "type", "all").count();
        System.out.println("Search counter: " + count);
    }
}
