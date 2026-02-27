package com.exercise.productapi.controller;

import com.exercise.productapi.dto.ProductRequest;
import com.exercise.productapi.dto.ProductResponse;
import com.exercise.productapi.exception.ResourceNotFoundException;
import com.exercise.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO 8: Add @WebMvcTest(ProductController.class) to this class.
//         @WebMvcTest loads ONLY the web layer (controllers, filters, etc.).
//         It does NOT load the full Spring context or database.
//         It's much faster than @SpringBootTest for testing controllers.
public class ProductControllerTest {

    // TODO 9: Add @Autowired to mockMvc.
    // MockMvc lets you simulate HTTP requests without a real server.
    //
    // Add @MockBean to productService.
    // @MockBean replaces the real ProductService in the Spring context with a
    // Mockito mock.
    // This means the controller gets a fake service — we control what it returns.
    private MockMvc mockMvc;

    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new ProductResponse();
        sampleResponse.setId(1L);
        sampleResponse.setName("Laptop");
        sampleResponse.setDescription("A powerful laptop");
        sampleResponse.setPrice(new BigDecimal("999.99"));
        sampleResponse.setCategory("Electronics");
        sampleResponse.setStock(50);
    }

    // TODO 10: Implement this test.
    // Stub:
    // when(productService.getAllProducts()).thenReturn(List.of(sampleResponse))
    // Perform: mockMvc.perform(get("/api/products"))
    // Expect: status().isOk()
    // Expect: jsonPath("$.length()").value(1)
    // Expect: jsonPath("$[0].name").value("Laptop")
    @Test
    void getAllProducts_returns200WithProductList() throws Exception {
        // TODO 10: implement this test
    }

    // TODO 11: Implement this test.
    // Stub: when(productService.getProductById(1L)).thenReturn(sampleResponse)
    // Perform: mockMvc.perform(get("/api/products/1"))
    // Expect: status 200, jsonPath "$.name" = "Laptop", jsonPath "$.id" = 1
    @Test
    void getProductById_whenExists_returns200() throws Exception {
        // TODO 11: implement this test
    }

    // TODO 12: Implement this test — the 404 scenario.
    // Stub: when(productService.getProductById(99L)).thenThrow(new
    // ResourceNotFoundException("Product", 99L))
    // Perform: mockMvc.perform(get("/api/products/99"))
    // Expect: status().isNotFound()
    @Test
    void getProductById_whenNotFound_returns404() throws Exception {
        // TODO 12: implement this test
    }
}
