package com.exercise.productcatalog.repository;

import com.exercise.productcatalog.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private final AtomicLong idCounter = new AtomicLong(1);
    private final Map<Long, Product> store = new HashMap<>();

    public ProductRepository() {
        save(new Product(null, "Laptop Pro", "High-performance laptop", 1299.99, "Electronics", 50));
        save(new Product(null, "Wireless Mouse", "Ergonomic wireless mouse", 29.99, "Electronics", 200));
        save(new Product(null, "Standing Desk", "Adjustable height desk", 599.99, "Furniture", 15));
        save(new Product(null, "Java Programming Book", "Learn Java from scratch", 49.99, "Books", 100));
        save(new Product(null, "Coffee Mug", "Large ceramic mug", 12.99, "Kitchen", 500));
    }

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

    // SOLUTION TODO 9
    public List<Product> findByCategory(String category) {
        return store.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // SOLUTION TODO 10
    public List<Product> findByNameContainingIgnoreCase(String name) {
        return store.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}
