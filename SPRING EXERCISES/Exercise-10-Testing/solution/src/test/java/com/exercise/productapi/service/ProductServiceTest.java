package com.exercise.productapi.service;

import com.exercise.productapi.dto.ProductRequest;
import com.exercise.productapi.dto.ProductResponse;
import com.exercise.productapi.entity.Product;
import com.exercise.productapi.exception.ResourceNotFoundException;
import com.exercise.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest testRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Laptop", "A powerful laptop", new BigDecimal("999.99"), "Electronics", 50);
        testProduct.setId(1L);

        testRequest = new ProductRequest();
        testRequest.setName("Laptop");
        testRequest.setDescription("A powerful laptop");
        testRequest.setPrice(new BigDecimal("999.99"));
        testRequest.setCategory("Electronics");
        testRequest.setStock(50);
    }

    @Test
    void getAllProducts_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(testProduct));

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void getProductById_whenExists_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductResponse result = productService.getProductById(1L);

        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("999.99"));
    }

    @Test
    void getProductById_whenNotFound_throwsResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_savesAndReturnsProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductResponse result = productService.createProduct(testRequest);

        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
