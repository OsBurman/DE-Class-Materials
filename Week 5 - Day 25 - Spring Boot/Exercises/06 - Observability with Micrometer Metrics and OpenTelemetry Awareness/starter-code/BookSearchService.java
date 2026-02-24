package com.library;

// TODO: Import @Service from org.springframework.stereotype
// TODO: Import Counter, MeterRegistry, Timer from io.micrometer.core.instrument

/**
 * Service that performs book searches and records metrics.
 *
 * TODO:
 *   1. Add @Service
 *   2. Declare fields:
 *        - private final MeterRegistry meterRegistry
 *        - private final Counter searchCounter
 *   3. Constructor(MeterRegistry meterRegistry):
 *        - Assign meterRegistry field
 *        - Build the counter:
 *            this.searchCounter = Counter.builder("library.book.searches")
 *                                        .tag("type", "all")
 *                                        .register(meterRegistry);
 *   4. searchBooks(String query):
 *        - Increment searchCounter: searchCounter.increment()
 *        - Time the work with a Timer:
 *            meterRegistry.timer("library.search.duration").record(() -> {
 *                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
 *            });
 *        - Return "Search results for: " + query
 */
public class BookSearchService {

    // TODO: declare fields

    // TODO: constructor

    public String searchBooks(String query) {
        // TODO: increment counter, record timing, return result
        return null;
    }
}
