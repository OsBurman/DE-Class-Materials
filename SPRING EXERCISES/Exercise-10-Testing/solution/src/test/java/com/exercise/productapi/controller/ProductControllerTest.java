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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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

    @Test
    void getAllProducts_returns200WithProductList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void getProductById_whenExists_returns200() throws Exception {
        when(productService.getProductById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void getProductById_whenNotFound_returns404() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }
}
