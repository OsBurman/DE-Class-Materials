package com.exercise.productcatalog.repository;

import com.exercise.productcatalog.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// This class acts as our fake "database" — an in-memory store.
// In Exercise 04 we'll replace this with a real JPA repository backed by a database.
@Repository
public class ProductRepository {

    // Auto-incrementing ID counter (thread-safe)
    private final AtomicLong idCounter = new AtomicLong(1);

    // Our in-memory "table" — a Map from ID → Product
    private final Map<Long, Product> store = new HashMap<>();

    // Pre-load some sample data so the API isn't empty on startup
    public ProductRepository() {
        save(new Product(null, "Laptop Pro", "High-performance laptop", 1299.99, "Electronics", 50));
        save(new Product(null, "Wireless Mouse", "Ergonomic wireless mouse", 29.99, "Electronics", 200));
        save(new Product(null, "Standing Desk", "Adjustable height desk", 599.99, "Furniture", 15));
        save(new Product(null, "Java Programming Book", "Learn Java from scratch", 49.99, "Books", 100));
        save(new Product(null, "Coffee Mug", "Large ceramic mug", 12.99, "Kitchen", 500));
    }

    // ─── Basic CRUD ───────────────────────────────────────────────────────────

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idCounter.getAndIncrement());
        }
        store.put(product.getId(), product);
        return product;
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    // ─── Custom Queries ───────────────────────────────────────────────────────

    // TODO 9: Implement findByCategory(String category)
    // Return all products whose category equals the given category
    // (case-insensitive).
    // Hint: Use stream().filter(...).collect(Collectors.toList())
    public List<Product> findByCategory(String category) {
        // your code here
        return Collections.emptyList();
    }

    // TODO 10: Implement findByNameContainingIgnoreCase(String name)
    // Return all products whose name CONTAINS the keyword (case-insensitive).
    // Hint: Use String.toLowerCase() and String.contains()
    public List<Product> findByNameContainingIgnoreCase(String name) {
        // your code here
        return Collections.emptyList();
    }
}
