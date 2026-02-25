package com.academy;

import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.*;

/**
 * Day 8 Part 2 — Stream API & DateTime API
 *
 * Theme: Sales Analytics Dashboard
 * Run: mvn compile exec:java
 */
public class Main {

    record Sale(String product, String region, double amount, LocalDate date) {}

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Day 8 Part 2 — Streams & DateTime Demo              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        List<Sale> sales = List.of(
            new Sale("Laptop",  "North",  1299.99, LocalDate.of(2024, 1, 15)),
            new Sale("Phone",   "South",   799.99, LocalDate.of(2024, 2, 20)),
            new Sale("Laptop",  "South",  1399.99, LocalDate.of(2024, 3, 10)),
            new Sale("Tablet",  "North",   499.99, LocalDate.of(2024, 1, 25)),
            new Sale("Phone",   "North",   849.99, LocalDate.of(2024, 4, 5)),
            new Sale("Tablet",  "East",    549.99, LocalDate.of(2024, 2, 14)),
            new Sale("Laptop",  "East",   1199.99, LocalDate.of(2024, 5, 1)),
            new Sale("Phone",   "East",    699.99, LocalDate.of(2024, 3, 30))
        );

        demoStreams(sales);
        demoStreamCollectors(sales);
        demoDateTime();
    }

    static void demoStreams(List<Sale> sales) {
        System.out.println("=== 1. Stream Pipeline (filter → map → collect) ===");

        // filter — only Laptop sales
        List<String> laptopRegions = sales.stream()
            .filter(s -> s.product().equals("Laptop"))
            .map(Sale::region)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("  Laptop regions: " + laptopRegions);

        // map + reduce
        double totalRevenue = sales.stream()
            .mapToDouble(Sale::amount)
            .sum();
        System.out.printf("  Total revenue: $%.2f%n", totalRevenue);

        // count
        long northSales = sales.stream().filter(s -> s.region().equals("North")).count();
        System.out.println("  North sales count: " + northSales);

        // min / max
        sales.stream()
             .max(Comparator.comparingDouble(Sale::amount))
             .ifPresent(s -> System.out.printf("  Biggest sale: %s $%.2f%n", s.product(), s.amount()));

        // anyMatch / allMatch / noneMatch
        boolean anyExpensive = sales.stream().anyMatch(s -> s.amount() > 1000);
        boolean allPositive  = sales.stream().allMatch(s -> s.amount() > 0);
        System.out.println("  Any sale > $1000: " + anyExpensive);
        System.out.println("  All amounts > 0:  " + allPositive);

        // flatMap (flatten nested lists)
        List<List<String>> nested = List.of(List.of("a","b"), List.of("c","d"), List.of("e"));
        List<String> flat = nested.stream().flatMap(Collection::stream).collect(Collectors.toList());
        System.out.println("  flatMap example: " + flat);
        System.out.println();
    }

    static void demoStreamCollectors(List<Sale> sales) {
        System.out.println("=== 2. Collectors — groupingBy, joining, toMap ===");

        // groupingBy — group sales by product
        Map<String, List<Sale>> byProduct = sales.stream()
            .collect(Collectors.groupingBy(Sale::product));
        byProduct.forEach((product, list) ->
            System.out.printf("  %-8s: %d sales, total=$%.2f%n", product, list.size(),
                list.stream().mapToDouble(Sale::amount).sum()));

        // groupingBy + counting
        Map<String, Long> countByRegion = sales.stream()
            .collect(Collectors.groupingBy(Sale::region, Collectors.counting()));
        System.out.println("  Count by region: " + countByRegion);

        // groupingBy + averaging
        Map<String, Double> avgByProduct = sales.stream()
            .collect(Collectors.groupingBy(Sale::product, Collectors.averagingDouble(Sale::amount)));
        System.out.println("  Avg price by product:");
        avgByProduct.forEach((p, avg) -> System.out.printf("    %-8s avg=$%.2f%n", p, avg));

        // joining
        String productList = sales.stream()
            .map(Sale::product).distinct().sorted()
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("  Products: " + productList);

        // summarizingDouble
        DoubleSummaryStatistics stats = sales.stream()
            .collect(Collectors.summarizingDouble(Sale::amount));
        System.out.printf("  Stats: count=%d min=$%.2f max=$%.2f avg=$%.2f%n",
            stats.getCount(), stats.getMin(), stats.getMax(), stats.getAverage());
        System.out.println();
    }

    static void demoDateTime() {
        System.out.println("=== 3. DateTime API ===");

        // Current date/time
        LocalDate      today   = LocalDate.now();
        LocalTime      now     = LocalTime.now();
        LocalDateTime  both    = LocalDateTime.now();
        System.out.println("  LocalDate:     " + today);
        System.out.println("  LocalTime:     " + now);
        System.out.println("  LocalDateTime: " + both);

        // Specific dates
        LocalDate courseStart = LocalDate.of(2024, 1, 8);
        LocalDate courseEnd   = LocalDate.of(2024, 3, 15);
        System.out.println("  Course start: " + courseStart);
        System.out.println("  Course end:   " + courseEnd);

        // Date arithmetic
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate lastMonth = today.minusMonths(1);
        System.out.println("  Next week:    " + nextWeek);
        System.out.println("  Last month:   " + lastMonth);

        // Period — date-based difference
        Period period = Period.between(courseStart, courseEnd);
        System.out.println("  Course duration: " + period.getMonths() + " months, " + period.getDays() + " days");
        System.out.println("  Days between:    " + ChronoUnit.DAYS.between(courseStart, courseEnd));

        // Duration — time-based difference
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end   = LocalTime.of(17, 30);
        Duration duration = Duration.between(start, end);
        System.out.println("  Work day hours: " + duration.toHours() + "h " + duration.toMinutesPart() + "m");

        // Formatting
        DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("  Formatted date: " + today.format(fmt1));
        System.out.println("  Formatted dt:   " + both.format(fmt2));

        // Parsing
        LocalDate parsed = LocalDate.parse("2024-09-01");
        LocalDate parsedFmt = LocalDate.parse("15/06/2024", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("  Parsed ISO:     " + parsed);
        System.out.println("  Parsed custom:  " + parsedFmt);

        // Day of week
        System.out.println("  Today is:       " + today.getDayOfWeek());
        System.out.println("  Is weekend:     " + (today.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                                    today.getDayOfWeek() == DayOfWeek.SUNDAY));

        System.out.println("\n✓ Streams & DateTime demo complete.");
    }
}
