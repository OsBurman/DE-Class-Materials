package com.academy.products.repository;

import com.academy.products.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory product data store.
 *
 * TODO Task 4: Implement all methods below.
 */
@Repository
public class ProductRepository {

    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    // Pre-load some sample data
    public ProductRepository() {
        save(Product.builder().name("Laptop Pro 15").description("High-performance laptop")
                .price(new java.math.BigDecimal("1299.99")).category("Electronics").stockQuantity(50).build());
        save(Product.builder().name("Wireless Headphones").description("Noise-cancelling over-ear")
                .price(new java.math.BigDecimal("249.99")).category("Electronics").stockQuantity(120).build());
        save(Product.builder().name("Standing Desk").description("Adjustable height desk")
                .price(new java.math.BigDecimal("599.00")).category("Furniture").stockQuantity(30).build());
        save(Product.builder().name("Java Design Patterns").description("Gang of Four reference")
                .price(new java.math.BigDecimal("49.99")).category("Books").stockQuantity(200).build());
    }

    // TODO Task 4: Return a new ArrayList of all values
    public List<Product> findAll() {
        return new ArrayList<>(); // TODO
    }

    // TODO Task 4: Return Optional.ofNullable(store.get(id))
    public Optional<Product> findById(Long id) {
        return Optional.empty(); // TODO
    }

    // TODO Task 4: If product has no id, assign one. Put in store. Return the
    // product.
    public Product save(Product product) {
        // TODO
        return product;
    }

    // TODO Task 4: Remove by id. Return true if existed, false otherwise.
    public boolean delete(Long id) {
        return false; // TODO
    }

    // TODO Task 4: Filter by category (case-insensitive)
    public List<Product> findByCategory(String category) {
        return new ArrayList<>(); // TODO
    }
}
