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

// TODO 1: Add @ExtendWith(MockitoExtension.class) to this class.
//         This enables Mockito annotations like @Mock and @InjectMocks.
//         Without this, @Mock fields won't be initialized.
public class ProductServiceTest {

    // TODO 2: Add @Mock annotation to productRepository.
    //         Mockito will create a mock (fake) implementation of ProductRepository.
    //         This means no real database is needed — we control what the mock returns.
    private ProductRepository productRepository;

    // TODO 3: Add @InjectMocks annotation to productService.
    //         Mockito will create ProductService and inject the @Mock productRepository into it.
    //         This is equivalent to: new ProductService(productRepository)
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

    // TODO 4: Implement this test.
    //         Use: when(productRepository.findAll()).thenReturn(List.of(testProduct))
    //         Then call productService.getAllProducts()
    //         Assert the result is not empty and has size 1
    @Test
    void getAllProducts_returnsAllProducts() {
        // TODO 4: implement this test
    }

    // TODO 5: Implement this test.
    //         Use: when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct))
    //         Call productService.getProductById(1L)
    //         Assert the returned ProductResponse has the name "Laptop"
    @Test
    void getProductById_whenExists_returnsProduct() {
        // TODO 5: implement this test
    }

    // TODO 6: Implement this test — tests the "not found" scenario.
    //         Use: when(productRepository.findById(99L)).thenReturn(Optional.empty())
    //         Assert that calling productService.getProductById(99L) throws ResourceNotFoundException
    //         Use: assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L))
    @Test
    void getProductById_whenNotFound_throwsResourceNotFoundException() {
        // TODO 6: implement this test
    }

    // TODO 7: Implement this test.
    //         Use: when(productRepository.save(any(Product.class))).thenReturn(testProduct)
    //         Call productService.createProduct(testRequest)
    //         Assert the result name equals "Laptop"
    //         Verify productRepository.save() was called exactly once:
    //         verify(productRepository, times(1)).save(any(Product.class))
    @Test
    void createProduct_savesAndReturnsProduct() {
        // TODO 7: implement this test
    }
}
