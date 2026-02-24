package com.library;

// TODO: Import @Component, CommandLineRunner
// TODO: Import MeterRegistry from io.micrometer.core.instrument

/**
 * Calls BookSearchService three times and prints the accumulated counter value.
 *
 * TODO:
 *   1. Add @Component and implement CommandLineRunner
 *   2. Inject BookSearchService and MeterRegistry via constructor
 *   3. In run():
 *        - Call bookSearchService.searchBooks("spring boot") three times
 *        - Retrieve the counter value:
 *            double count = meterRegistry.counter("library.book.searches", "type", "all").count();
 *        - Print: "Search counter: " + count
 */
public class MetricsRunner {

    // TODO: declare fields

    // TODO: constructor

    // TODO: implement run(String... args)
}
