package com.exercise.productapi.repository;

import com.exercise.productapi.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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

    @Test
    void findByNameContainingIgnoreCase_returnsMatchingProducts() {
        List<Product> results = productRepository.findByNameContainingIgnoreCase("laptop");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Product::getName)
                .containsExactlyInAnyOrder("Gaming Laptop", "Office Laptop");
    }

    @Test
    void findByCategory_returnsCorrectProducts() {
        List<Product> results = productRepository.findByCategory("Electronics");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Product::getName).contains("Gaming Laptop");
    }
}
