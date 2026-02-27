package com.exercise.productapi.repository;

import com.exercise.productapi.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// TODO 13: Add @DataJpaTest to this class.
//          @DataJpaTest loads ONLY the JPA layer: entities, repositories, and an in-memory H2 database.
//          It does NOT load controllers, services, or the full Spring context.
//          Each test method runs in a transaction that is rolled back after the test.
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productRepository.save(new Product("Gaming Laptop", "High-end gaming laptop", new BigDecimal("1499.99"), "Electronics", 20));
        productRepository.save(new Product("Office Laptop", "Business laptop", new BigDecimal("799.99"), "Electronics", 30));
        productRepository.save(new Product("Mechanical Keyboard", "RGB keyboard", new BigDecimal("89.99"), "Accessories", 100));
        productRepository.save(new Product("Mouse", "Wireless mouse", new BigDecimal("39.99"), "Accessories", 150));
    }

    // TODO 14: Test findByNameContainingIgnoreCase.
    //          Search for "laptop" (lowercase) and assert 2 products are returned.
    //          Use assertThat(results).hasSize(2)
    @Test
    void findByNameContainingIgnoreCase_returnsMatchingProducts() {
        // TODO 14: implement this test
    }

    // TODO 15: Test findByCategory.
    //          Find all "Electronics" products and assert 2 are returned.
    //          Also assert one of them has name "Gaming Laptop":
    //          assertThat(results).extracting(Product::getName).contains("Gaming Laptop")
    @Test
    void findByCategory_returnsCorrectProducts() {
        // TODO 15: implement this test
    }
}
